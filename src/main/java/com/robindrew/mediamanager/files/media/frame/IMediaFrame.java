package com.robindrew.mediamanager.files.media.frame;

import java.awt.image.BufferedImage;
import java.io.File;

public interface IMediaFrame {

	double getTimestamp();

	File getFile();

	BufferedImage toBufferedImage();

	byte[] toByteArray();

	void writeTo(File file);
}
