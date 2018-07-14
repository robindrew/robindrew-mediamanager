package com.robindrew.mediamanager.files.media.tag;

import java.util.Set;

public interface IMediaFileTagCache extends Iterable<IMediaFileTag> {

	Set<String> getTagNames();

	Set<String> getTagNames(int id);

	Set<IMediaFileTag> getTags(String name);

	void add(IMediaFileTag tag);

}
