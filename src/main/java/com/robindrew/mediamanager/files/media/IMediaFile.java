package com.robindrew.mediamanager.files.media;

import java.util.Set;

public interface IMediaFile {

	String getSourcePath();

	boolean isArchived();

	int getId();

	String getPath();

	String getName();

	MediaFileType getType();

	Set<String> getTags();

}
