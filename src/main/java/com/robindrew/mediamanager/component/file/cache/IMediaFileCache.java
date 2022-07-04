package com.robindrew.mediamanager.component.file.cache;

import java.util.Set;

public interface IMediaFileCache {

	Set<IMediaFile> getAll();

	IMediaFile getMediaFile(String path, MediaFileType type);

	void persistAll();

	boolean containsPath(String path);

}
