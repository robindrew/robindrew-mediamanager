package com.robindrew.mediamanager.files.manager;

import java.io.File;
import java.util.Set;

import com.robindrew.mediamanager.files.media.IMediaFile;

public interface IFileManager {

	File getRootDirectory();

	Set<IMediaFile> getMediaFiles();

	IMediaFile getMediaFile(int id);

}
