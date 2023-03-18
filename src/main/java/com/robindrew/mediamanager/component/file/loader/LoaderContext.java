package com.robindrew.mediamanager.component.file.loader;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;

import com.robindrew.common.image.ImageFormat;
import com.robindrew.mediamanager.component.file.cache.IMediaFile;

public class LoaderContext {

	private final IMediaFile file;
	private int width = 0;
	private int height = 0;
	private boolean fit = true;
	private BigDecimal frameSeconds = ZERO;
	private BigDecimal frameDuration = ZERO;

	public LoaderContext(IMediaFile file) {
		if (file == null) {
			throw new NullPointerException("file");
		}
		this.file = file;
	}

	public LoaderContext(IMediaFile file, int width, int height) {
		this(file);
		setWidth(width);
		setHeight(height);
	}

	public IMediaFile getFile() {
		return file;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isFit() {
		return fit;
	}

	public BigDecimal getFrameSeconds() {
		return frameSeconds;
	}

	public BigDecimal getFrameDuration() {
		return frameDuration;
	}

	public ImageFormat getImageFormat() {
		if (!frameDuration.equals(ZERO)) {
			return ImageFormat.GIF;
		}
		return ImageFormat.JPG;
	}

	public LoaderContext setWidth(int width) {
		this.width = width;
		return this;
	}

	public LoaderContext setHeight(int height) {
		this.height = height;
		return this;
	}

	public LoaderContext setFit(boolean fit) {
		this.fit = fit;
		return this;
	}

	public LoaderContext setFrameSeconds(BigDecimal seconds) {
		this.frameSeconds = seconds;
		return this;
	}

	public LoaderContext setFrameSeconds(int seconds) {
		this.frameSeconds = new BigDecimal(seconds);
		return this;
	}

	public LoaderContext setFrameDuration(BigDecimal seconds) {
		this.frameDuration = seconds;
		return this;
	}

	public LoaderContext setFrameDuration(int seconds) {
		this.frameDuration = new BigDecimal(seconds);
		return this;
	}

	public ImageKey toKey() {
		return new ImageKey(file.getId(), width, height, frameSeconds, frameDuration);
	}

}
