package com.robindrew.mediamanager.files.media.tag.file;

import com.robindrew.mediamanager.files.media.tag.ITag;

public interface IMediaFileTag extends Comparable<IMediaFileTag> {

	int getFileId();

	ITag getTag();

}
