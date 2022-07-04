package com.robindrew.mediamanager.component.file.cache;

import java.util.Set;

public interface IMediaFileCollection {

	String getName();

	String getUrlEncodedName();

	int size();

	int tags();

	Set<IMediaFile> getFiles();

	boolean contains(int id);

}
