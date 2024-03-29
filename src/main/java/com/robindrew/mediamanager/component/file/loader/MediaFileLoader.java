package com.robindrew.mediamanager.component.file.loader;

import static java.math.BigDecimal.ZERO;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Stopwatch;
import com.google.common.io.ByteStreams;
import com.robindrew.common.base.Java;
import com.robindrew.common.base.Preconditions;
import com.robindrew.common.base.Threads;
import com.robindrew.common.image.IImageOutput;
import com.robindrew.common.image.ImageFormat;
import com.robindrew.common.image.ImageOutput;
import com.robindrew.common.image.Images;
import com.robindrew.common.io.file.Files;
import com.robindrew.mediamanager.component.file.cache.IMediaFile;
import com.robindrew.mediamanager.component.file.cache.MediaType;
import com.robindrew.mediamanager.component.file.loader.frame.AnimatedGifWriter;
import com.robindrew.mediamanager.component.file.loader.frame.AnimatedGifWriterQueue;
import com.robindrew.mediamanager.component.file.loader.frame.MediaFrame;
import com.robindrew.mediamanager.component.file.manager.IMediaFileManager;

@Component
public class MediaFileLoader implements IMediaFileLoader {

	private static final Logger log = LoggerFactory.getLogger(MediaFileLoader.class);

	public static final BufferedImage convertColorspace(BufferedImage image, int newType) {
		BufferedImage raw_image = image;
		image = new BufferedImage(raw_image.getWidth(), raw_image.getHeight(), newType);
		ColorConvertOp xformOp = new ColorConvertOp(null);
		xformOp.filter(raw_image, image);
		return image;
	}

	public static final BufferedImage resize(BufferedImage image, int width, int height) {

		int oldWidth = image.getWidth();
		int oldHeight = image.getHeight();
		int newWidth = oldWidth;
		int newHeight = oldHeight;

		Image scaled = image;
		if (newWidth > width || newHeight > height) {

			// Factors
			float widthFactor = (float) oldWidth / (float) width;
			float heightFactor = (float) oldHeight / (float) height;
			float factor = widthFactor > heightFactor ? widthFactor : heightFactor;
			newWidth = (int) (oldWidth / factor);
			newHeight = (int) (oldHeight / factor);

			// Scale
			scaled = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		} else {
			return image;
		}

		BufferedImage newImage = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_RGB);
		newImage.getGraphics().drawImage(scaled, 0, 0, null);
		return newImage;
	}

	private final IMediaFileManager manager;
	private final Map<ImageKey, Reference<ImageData>> imageCache = new ConcurrentHashMap<>();
	private final File cacheDirectory;
	private final AnimatedGifWriterQueue writerQueue;

	public MediaFileLoader(@Autowired IMediaFileManager manager, @Value("${cache.file.directory}") File cacheDirectory, @Autowired AnimatedGifWriterQueue writerQueue) {
		this.writerQueue = writerQueue;
		this.manager = Preconditions.notNull("manager", manager);
		this.cacheDirectory = Preconditions.existsDirectory("cacheDirectory", cacheDirectory);
	}

	@PostConstruct
	public void load() {
		log.info("Loading Media Files in {} ...", cacheDirectory);
		Stopwatch timer = Stopwatch.createStarted();
		Set<IMediaFile> files = manager.getMediaFiles();
		timer.stop();
		log.info("Loaded {} Media Files in {}", files.size(), timer);

		// Asynchronously load all files
		int threads = 4;
		loadImagesInBackground(files, threads);

	}

	private void loadImagesInBackground(Set<IMediaFile> files, int threads) {
		ExecutorService pool = Threads.newFixedThreadPool("BackgroundImageLoader-%d", threads);
		for (IMediaFile file : files) {
			if (file.getType().getType().equals(MediaType.PHOTO)) {
				pool.submit(new Runnable() {

					@Override
					public void run() {
						getImage(new LoaderContext(file, 320, 240));
					}
				});
			}
		}
	}

	public byte[] readImageFromFile(IMediaFile mediaFile, File file) {
		try {
			if (mediaFile.isArchived()) {
				try (ZipFile zip = new ZipFile(file)) {
					ZipEntry entry = zip.getEntry(mediaFile.getName());
					try (InputStream input = zip.getInputStream(entry)) {
						return ByteStreams.toByteArray(input);
					}
				}
			}

			return Files.readToBytes(file);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	public ImageData getImageData(LoaderContext context) {
		try {

			final IMediaFile mediaFile = context.getFile();
			final File file = new File(manager.getRootDirectory(), mediaFile.getSourcePath());
			final BufferedImage image;

			// Photo
			if (mediaFile.getType().isPhoto()) {
				byte[] imageData = readImageFromFile(mediaFile, file);
				if (context.getWidth() == 0 && context.getHeight() == 0) {
					return new ImageData(imageData);
				}
				image = Images.toBufferedImage(imageData);
			}

			// Video
			else {
				BigDecimal duration = context.getFrameDuration();
				if (duration.equals(ZERO)) {
					image = new MediaFrame(file, context.getFrameSeconds()).toBufferedImage();
				} else {
					try {
						double fromSecond = context.getFrameSeconds().doubleValue();
						double toSecond = fromSecond + context.getFrameDuration().doubleValue();
						AnimatedGifWriter writer = new AnimatedGifWriter(file);
						writer.setFromSecond(fromSecond);
						writer.setToSecond(toSecond);
						
						Future<byte[]> future = writerQueue.enqueue(writer);
						byte[] imageData = future.get();
						return new ImageData(imageData, ImageFormat.GIF);
					} catch (Exception e) {
						log.warn("Failed to write animated GIF for file " + file, e);
						context.setFrameDuration(ZERO);
						image = new MediaFrame(file, context.getFrameSeconds()).toBufferedImage();
					}
				}
			}

			byte[] data = resizeImage(image, context).writeToByteArray();
			return new ImageData(data, context.getImageFormat());

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	private static final IImageOutput resizeImage(BufferedImage image, LoaderContext context) throws IOException {
		int width = context.getWidth();
		int height = context.getHeight();
		boolean fit = context.isFit();

		if (width > 0 && height > 0) {
			if (fit) {
				image = Images.scaleImageToFit(image, width, height, Color.WHITE);
			} else {
				image = Images.scaleImageToFit(image, width, height);
			}
		}
		return new ImageOutput(image, context.getImageFormat());
	}

	@Override
	public ImageData getImage(LoaderContext context) {

		ImageKey key = context.toKey();
		Reference<ImageData> reference = imageCache.get(key);
		ImageData image = reference == null ? null : reference.get();
		if (image == null) {

			// Read from file system
			image = readFromDirectory(key, context.getImageFormat());

			if (image == null) {
				Stopwatch timer = Stopwatch.createStarted();
				image = getImageData(context);
				timer.stop();
				log.debug("Image from '{}' loaded in {}", context.getFile().getPath(), timer);
			}

			reference = new SoftReference<>(image);

			imageCache.put(key, reference);
			writeToDirectory(key, image);
		}
		return image;
	}

	private ImageData readFromDirectory(ImageKey key, ImageFormat format) {
		File file = new File(cacheDirectory, getFilename(key, format));
		if (!file.exists()) {
			return null;
		}
		byte[] image = Files.readToBytes(file);
		return new ImageData(image, file);
	}

	private String getFilename(ImageKey key, ImageFormat format) {
		StringBuilder name = new StringBuilder();
		name.append(key.getWidth()).append("x");
		name.append(key.getHeight()).append("/");
		name.append(key.getId() / 100).append("/");
		name.append(key.getId());
		if (key.hasSeconds()) {
			name.append("-").append(key.getSeconds());
		}
		if (key.hasDuration()) {
			name.append("-").append(key.getDuration());
		}
		name.append(".").append(format.name().toLowerCase());
		return name.toString();
	}

	private void writeToDirectory(ImageKey key, ImageData data) {
		File file = new File(cacheDirectory, getFilename(key, data.getFormat()));
		file.getParentFile().mkdirs();
		Files.writeFromBytes(file, data.getImage());
	}

}
