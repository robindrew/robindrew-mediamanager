package com.robindrew.mediamanager.files.media;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MultimapBuilder.SetMultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.robindrew.common.base.Preconditions;
import com.robindrew.common.text.Strings;

public class MediaFileCollection implements IMediaFileCollection {

	public static List<IMediaFileCollection> splitToListWithType(MediaType type, Collection<? extends IMediaFile> files) {

		SetMultimap<String, IMediaFile> collectionMap = SetMultimapBuilder.treeKeys().linkedHashSetValues().build();
		for (IMediaFile file : files) {
			if (file.getType().getType().equals(type)) {
				collectionMap.put(getKey(file), file);
			}
		}

		List<IMediaFileCollection> collections = new ArrayList<>();
		for (String key : collectionMap.keySet()) {
			collections.add(new MediaFileCollection(key, collectionMap.get(key)));
		}
		return collections;
	}

	private static String getKey(IMediaFile file) {
		if (file.isArchived()) {
			return file.getSourcePath();
		}
		String path = file.getPath();
		int slash = path.lastIndexOf('/');
		if (slash != -1) {
			path = path.substring(0, slash);
		}
		return path;
	}

	private final String name;
	private final Set<IMediaFile> files;

	public MediaFileCollection(String name, Collection<? extends IMediaFile> files) {
		this.name = Preconditions.notEmpty("name", name);
		this.files = ImmutableSet.copyOf(files);
	}

	@Override
	public Set<IMediaFile> getFiles() {
		return files;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUrlEncodedName() {
		return Strings.urlEncode(getName());
	}

	@Override
	public int size() {
		return getFiles().size();
	}

	@Override
	public int tags() {
		int count = 0;
		for (IMediaFile file : getFiles()) {
			count += file.getTags().size();
		}
		return count;
	}

	@Override
	public boolean contains(int id) {
		for (IMediaFile file : getFiles()) {
			if (file.getId() == id) {
				return true;
			}
		}
		return false;
	}

}
