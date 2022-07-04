package com.robindrew.mediamanager.component.file.cache;

import java.util.Set;

import com.robindrew.mediamanager.component.tag.ITag;

public interface IMediaFile {

	String getSourcePath();

	boolean isArchived();

	int getId();

	String getPath();

	String getName();

	MediaFileType getType();

	Set<ITag> getTags();

}
