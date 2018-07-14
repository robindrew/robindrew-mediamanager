package com.robindrew.mediamanager.files.media.loader;

import com.robindrew.mediamanager.files.media.IMediaFile;

public interface IMediaFileLoader {

	byte[] getImage(IMediaFile mediaFile, int width, int height, boolean fit);

}
