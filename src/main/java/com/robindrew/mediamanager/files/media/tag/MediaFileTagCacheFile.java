package com.robindrew.mediamanager.files.media.tag;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MultimapBuilder.SetMultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.robindrew.common.io.file.objectstore.CachedObjectStoreFile;

public class MediaFileTagCacheFile extends CachedObjectStoreFile<IMediaFileTag> implements IMediaFileTagCache {

	private static final Logger log = LoggerFactory.getLogger(MediaFileTagCacheFile.class);

	private final SetMultimap<String, IMediaFileTag> nameCache = SetMultimapBuilder.treeKeys().treeSetValues().build();
	private final SetMultimap<Integer, IMediaFileTag> idCache = SetMultimapBuilder.treeKeys().treeSetValues().build();

	public MediaFileTagCacheFile(File file) {
		super(file);

		List<IMediaFileTag> tags = getAll();
		log.info("Loaded {} tags", tags.size());
		updateMaps();
	}

	@Override
	public List<IMediaFileTag> getAll() {
		synchronized (this) {
			return super.getAll();
		}
	}

	@Override
	public void setAll(Collection<? extends IMediaFileTag> elements) {
		synchronized (this) {
			super.setAll(elements);
			updateMaps();
		}
	}

	private void updateMaps() {
		synchronized (this) {
			nameCache.clear();
			idCache.clear();
			for (IMediaFileTag tag : getAll()) {
				nameCache.put(tag.getName(), tag);
				idCache.put(tag.getId(), tag);
			}
		}
	}

	@Override
	public Set<String> getTagNames() {
		synchronized (this) {
			return ImmutableSet.copyOf(nameCache.keySet());
		}
	}

	@Override
	public Set<IMediaFileTag> getTags(String name) {
		synchronized (this) {
			return ImmutableSet.copyOf(nameCache.get(name));
		}
	}

	@Override
	public void add(IMediaFileTag tag) {
		synchronized (this) {
			Set<IMediaFileTag> set = new LinkedHashSet<>(getAll());
			int size = set.size();
			set.add(tag);
			if (set.size() > size) {
				setAll(set);
			}
		}
	}

	@Override
	public Set<String> getTagNames(int id) {
		Set<IMediaFileTag> tags;
		synchronized (this) {
			tags = idCache.get(id);
		}
		if (tags.isEmpty()) {
			return Collections.emptySet();
		}
		Set<String> names = new TreeSet<>();
		for (IMediaFileTag tag : tags) {
			names.add(tag.getName());
		}
		return names;
	}

	@Override
	public IMediaFileTag parseFromLine(String line) {
		if (line.isEmpty()) {
			return null;
		}
		List<String> elements = Splitter.on(',').omitEmptyStrings().trimResults().splitToList(line);
		if (elements.size() != 2) {
			log.warn("Invalid Line: '" + line + "'");
			return null;
		}

		int id = Integer.parseInt(elements.get(0));
		String name = elements.get(1);
		return new MediaFileTag(id, name);
	}

	@Override
	public String formatToLine(IMediaFileTag element) {
		return element.getId() + "," + element.getName();
	}

}
