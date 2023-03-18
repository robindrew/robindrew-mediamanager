package com.robindrew.mediamanager.component.file.loader.frame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;

public interface IMediaFrame {

	BigDecimal getSeconds();

	File getFile();

	BufferedImage toBufferedImage();

	byte[] toByteArray();

	void writeTo(File file);
}
