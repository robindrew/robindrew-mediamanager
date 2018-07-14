package com.robindrew.mediamanager.files.media.loader;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.robindrew.common.image.IImageOutput;
import com.robindrew.common.image.ImageFormat;
import com.robindrew.common.image.ImageOutput;
import com.robindrew.common.image.ImageResizer;
import com.robindrew.common.image.Images;
import com.robindrew.common.io.Files;
import com.robindrew.common.util.Check;
import com.robindrew.common.util.Java;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;

public class MediaFileLoader implements IMediaFileLoader {

	public static void main(String[] args) throws IOException {
		File fromFile = new File("C:/temp/shard/Photo 0010.jpg");
		File toFile = new File("C:/temp/shard/Photo 0010a.jpg");
		toFile.delete();

		byte[] bytes = Files.readToBytes(fromFile);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		ImageResizer resizer = new ImageResizer();
		resizer.setBackground(Color.WHITE);
		image = resize(image, 320, 240);
		System.out.println(image.getType());
		image = convertColorspace(image, BufferedImage.TYPE_INT_RGB);
		ImageOutput output = new ImageOutput(image, ImageFormat.JPG);
		output.writeToFile(toFile);
	}

	final public static BufferedImage convertColorspace(BufferedImage image, int newType) {
		BufferedImage raw_image = image;
		image = new BufferedImage(raw_image.getWidth(), raw_image.getHeight(), newType);
		ColorConvertOp xformOp = new ColorConvertOp(null);
		xformOp.filter(raw_image, image);
		return image;
	}

	final static BufferedImage resize(BufferedImage image, int width, int height) {

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

	private static final Logger log = LoggerFactory.getLogger(MediaFileLoader.class);

	private final IFileManager manager;
	private final Map<ImageKey, Reference<byte[]>> imageCache = new ConcurrentHashMap<>();
	private final File cacheDirectory;

	public MediaFileLoader(IFileManager manager, File cacheDirectory) {
		this.manager = Check.notNull("manager", manager);
		this.cacheDirectory = Check.existsDirectory("cacheDirectory", cacheDirectory);
	}

	public BufferedImage readImageFromFile(IMediaFile mediaFile) {
		try {
			File file = new File(manager.getRootDirectory(), mediaFile.getSourcePath());

			if (mediaFile.isArchived()) {
				try (ZipFile zip = new ZipFile(file)) {
					ZipEntry entry = zip.getEntry(mediaFile.getName());
					try (InputStream input = zip.getInputStream(entry)) {
						return Images.toBufferedImage(input);
					}
				}
			}

			return Images.toBufferedImage(file);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	public byte[] getImageData(IMediaFile mediaFile, int width, int height, boolean fit) {
		try {
			BufferedImage image = readImageFromFile(mediaFile);

			IImageOutput output = resizeImage(image, width, height, fit);
			return output.writeToByteArray();
			//
			// ByteArrayInputStream input = new ByteArrayInputStream(bytes);
			// ByteArrayOutputStream output = new ByteArrayOutputStream();
			// Thumbnailator.createThumbnail(input, output, width, height);
			// return output.toByteArray();

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	protected IImageOutput resizeImage(BufferedImage image, int width, int height, boolean fit) throws IOException {
		if (width > 0 && height > 0) {
			if (fit) {
				image = Images.scaleImageToFit(image, width, height, Color.WHITE);
			} else {
				image = Images.scaleImageToFit(image, width, height);
			}
		}
		return new ImageOutput(image, ImageFormat.JPG);
	}

	@Override
	public byte[] getImage(IMediaFile mediaFile, int width, int height, boolean fit) {

		ImageKey key = new ImageKey(mediaFile.getId(), width, height);
		Reference<byte[]> reference = imageCache.get(key);
		byte[] image = reference == null ? null : reference.get();
		if (image == null) {

			// Read from file system
			image = readFromDirectory(key);

			if (image == null) {
				Stopwatch timer = Stopwatch.createStarted();
				image = getImageData(mediaFile, width, height, fit);
				timer.stop();
				log.debug("Image {} loaded in {}", mediaFile.getPath(), timer);
			}

			reference = new SoftReference<>(image);

			imageCache.put(key, reference);
			writeToDirectory(key, image);
		}
		return image;
	}

	private byte[] readFromDirectory(ImageKey key) {
		File file = new File(cacheDirectory, key.getWidth() + "x" + key.getHeight() + "/" + (key.getId() / 100) + "/" + key.getId() + ".jpg");
		if (file.exists()) {
			return Files.readToBytes(file);
		}
		return null;
	}

	private void writeToDirectory(ImageKey key, byte[] image) {
		File file = new File(cacheDirectory, key.getWidth() + "x" + key.getHeight() + "/" + (key.getId() / 100) + "/" + key.getId() + ".jpg");
		file.getParentFile().mkdirs();
		Files.writeFromBytes(file, image);
	}

}
