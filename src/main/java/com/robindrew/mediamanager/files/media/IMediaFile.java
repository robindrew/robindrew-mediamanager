package com.robindrew.mediamanager.files.media;

import java.io.File;
import java.util.Set;

import com.robindrew.mediamanager.files.media.tag.ITag;

public interface IMediaFile {

	String getSourcePath();

	boolean isArchived();

	int getId();

	String getPath();

	String getName();

	MediaFileType getType();

	Set<ITag> getTags();

	IMediaFrame getFrame(File file, double timestamp);

}
