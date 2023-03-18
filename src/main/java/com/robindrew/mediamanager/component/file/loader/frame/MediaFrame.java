package com.robindrew.mediamanager.component.file.loader.frame;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;

import javax.imageio.ImageIO;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import com.google.common.base.Throwables;

public class MediaFrame implements IMediaFrame {

	private final File file;
	private final BigDecimal seconds;
	private final Picture picture;

	public MediaFrame(File file, BigDecimal seconds) {
		this.file = file;
		this.seconds = seconds;
		try {
			this.picture = FrameGrab.getFrameAtSec(file, seconds.doubleValue());
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	public MediaFrame(File file, int frameNumber) {
		this.file = file;
		this.seconds = new BigDecimal(frameNumber);
		try {
			this.picture = FrameGrab.getFrameFromFile(file, frameNumber);
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	public BufferedImage toBufferedImage() {
		return AWTUtil.toBufferedImage(picture);
	}

	public byte[] toByteArray() {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(toBufferedImage(), "jpg", output);
			return output.toByteArray();
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	public void writeTo(File file) {
		try {
			ImageIO.write(toBufferedImage(), "jpg", file);
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	@Override
	public BigDecimal getSeconds() {
		return seconds;
	}

	@Override
	public File getFile() {
		return file;
	}
}
