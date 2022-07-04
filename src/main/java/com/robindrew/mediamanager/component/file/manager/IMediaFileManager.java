package com.robindrew.mediamanager.component.file.manager;

import java.io.File;
import java.util.Set;

import com.robindrew.mediamanager.component.file.cache.IMediaFile;

public interface IMediaFileManager {

	File getRootDirectory();

	Set<IMediaFile> getMediaFiles();

	IMediaFile getMediaFile(int id);

}
