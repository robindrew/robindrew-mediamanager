package com.robindrew.mediamanager.files.media;

import java.util.Set;

public interface IMediaFileCollection {

	String getName();

	String getUrlEncodedName();

	int size();

	int tags();

	Set<IMediaFile> getFiles();

	boolean contains(int id);

}
