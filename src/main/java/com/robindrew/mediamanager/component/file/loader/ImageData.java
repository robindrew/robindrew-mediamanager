package com.robindrew.mediamanager.component.file.loader;

import java.io.File;

import com.robindrew.common.image.ImageFormat;

public class ImageData {

	private static ImageFormat getImageFormat(File file) {
		String name = file.getName().toLowerCase();
		if (name.endsWith(".jpg")) {
			return ImageFormat.JPG;
		}
		if (name.endsWith(".gif")) {
			return ImageFormat.GIF;
		}
		throw new IllegalArgumentException("Unable to determine image format from file: " + file);
	}

	private final byte[] image;
	private final ImageFormat format;

	public ImageData(byte[] image, ImageFormat format) {
		this.image = image;
		this.format = format;
	}

	public ImageData(byte[] image) {
		this(image, ImageFormat.JPG);
	}

	public ImageData(byte[] image, File file) {
		this(image, getImageFormat(file));
	}

	public byte[] getImage() {
		return image;
	}

	public ImageFormat getFormat() {
		return format;
	}
}
