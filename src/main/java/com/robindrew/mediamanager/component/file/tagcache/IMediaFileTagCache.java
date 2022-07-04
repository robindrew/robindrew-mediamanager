package com.robindrew.mediamanager.component.file.tagcache;

import java.util.Set;

import com.robindrew.mediamanager.component.tag.ITag;

public interface IMediaFileTagCache extends Iterable<IMediaFileTag> {

	Set<ITag> getTags();

	Set<ITag> getTags(int fileId);

	Set<IMediaFileTag> getFileTags(String name);

	Set<IMediaFileTag> getFileTags(ITag tag);

	void add(IMediaFileTag tag);

	void remove(IMediaFileTag tag);

	void remove(int fileId, String name);

	void removeAll(int fileId);

	void add(int fileId, String tag);

}
