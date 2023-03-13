package com.robindrew.mediamanager.component.file.loader.frame;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.robindrew.common.base.Java;
import com.robindrew.common.image.Images;

public class AnimatedGifWriter {

	private static final Logger log = LoggerFactory.getLogger(AnimatedGifWriter.class);

	private final File outputFile;
	private int maxWidth = 640;
	private int maxHeight = 480;

	public AnimatedGifWriter(File outputFile) {
		this.outputFile = outputFile;
	}

	public AnimatedGifWriter(String outputFile) {
		this.outputFile = new File(outputFile);
	}

	public File getOutputFile() {
		return outputFile;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	private BufferedImage toBufferedImage(Picture picture) {
		BufferedImage image = AWTUtil.toBufferedImage(picture);
		image = Images.scaleImageToFit(image, maxWidth, maxHeight);
		return image;
	}

	public void writeFrames(String inputFile, double seconds) {
		writeFrames(new File(inputFile), seconds);
	}

	public void writeFrames(File inputFile, double seconds) {
		writeFrames(inputFile, 0.0, seconds);
	}

	public void writeFrames(String inputFile, double fromSecond, double toSecond) {
		writeFrames(new File(inputFile), fromSecond, toSecond);
	}

	public void writeFrames(File inputFile, double fromSecond, double toSecond) {
		if (fromSecond < 0.0) {
			throw new IllegalArgumentException("fromSecond=" + fromSecond);
		}
		if (toSecond <= fromSecond) {
			throw new IllegalArgumentException("fromSecond=" + fromSecond + ", toSecond=" + toSecond);
		}

		// We are limited to 10 frames a second max, code doesn't work properly otherwise
		log.info("[Writing] {} ({} s -> {} s)", outputFile, fromSecond, toSecond);
		Stopwatch timer = Stopwatch.createStarted();
		int frameCount = 0;
		try (FileChannelWrapper channel = NIOUtils.readableChannel(inputFile)) {
			FrameGrab frames = FrameGrab.createFrameGrab(channel);

			AnimatedGifEncoder encoder = new AnimatedGifEncoder();
			try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile))) {
				encoder.start(output);
				encoder.setDelay(100); // 10 frames a second

				long minSecond = 0;
				double second = fromSecond;
				while (second < toSecond) {
					Picture frame = frames.seekToSecondPrecise(second).getNativeFrame();
					encoder.addFrame(toBufferedImage(frame));
					frameCount++;
					second = fromSecond + (frameCount * 0.1); // 10 frames a second
					long elapsed = timer.elapsed(TimeUnit.SECONDS);
					if (minSecond < elapsed && elapsed % 5 == 0) {
						minSecond = elapsed;
						log.info("[Writing] {} ({} frames in {})", outputFile, frameCount, timer);
					}
				}
				encoder.finish();
			}
		} catch (Exception e) {
			throw Java.propagate(e);
		}
		timer.stop();
		log.info("[Written] {} ({} frames in {})", outputFile, frameCount, timer);
	}
}
