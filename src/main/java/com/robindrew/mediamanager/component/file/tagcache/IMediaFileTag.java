package com.robindrew.mediamanager.component.file.tagcache;

import com.robindrew.mediamanager.component.tag.ITag;

public interface IMediaFileTag extends Comparable<IMediaFileTag> {

	int getFileId();

	ITag getTag();

}
