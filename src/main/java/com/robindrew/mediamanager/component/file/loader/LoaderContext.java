package com.robindrew.mediamanager.component.file.loader;

import com.robindrew.mediamanager.component.file.cache.IMediaFile;

public class LoaderContext {

	private final IMediaFile file;
	private int width = 0;
	private int height = 0;
	private boolean fit = true;
	private double frameSeconds = 5.0;

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

	public double getFrameSeconds() {
		return frameSeconds;
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

	public LoaderContext setFrameSeconds(double frameSeconds) {
		this.frameSeconds = frameSeconds;
		return this;
	}

	public ImageKey toKey() {
		return new ImageKey(file.getId(), width, height);
	}

}
