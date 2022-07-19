package com.robindrew.mediamanager.component.file.tagcache;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MultimapBuilder.SetMultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.robindrew.common.io.file.objectstore.CachedObjectStoreFile;
import com.robindrew.mediamanager.component.tag.ITag;
import com.robindrew.mediamanager.component.tag.ITagCache;

@Component
public class MediaFileTagCacheFile extends CachedObjectStoreFile<IMediaFileTag> implements IMediaFileTagCache {

	private static final Logger log = LoggerFactory.getLogger(MediaFileTagCacheFile.class);

	private final ITagCache tagCache;
	private final SetMultimap<String, IMediaFileTag> tagNameCache = SetMultimapBuilder.treeKeys().treeSetValues().build();
	private final SetMultimap<Integer, IMediaFileTag> fileIdCache = SetMultimapBuilder.treeKeys().treeSetValues().build();

	public MediaFileTagCacheFile(ITagCache tagCache, @Value("${media.tag.cache.file}") File file) {
		super(file);
		this.tagCache = tagCache;
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
			tagNameCache.clear();
			fileIdCache.clear();
			for (IMediaFileTag tag : getAll()) {
				tagNameCache.put(tag.getTag().getName(), tag);
				fileIdCache.put(tag.getFileId(), tag);
			}
		}
	}

	@Override
	public Set<ITag> getTags() {
		return tagCache.getTags();
	}

	@Override
	public Set<IMediaFileTag> getFileTags(String name) {
		synchronized (this) {
			return ImmutableSet.copyOf(tagNameCache.get(name));
		}
	}

	@Override
	public Set<IMediaFileTag> getFileTags(ITag tag) {
		return getFileTags(tag.getName());
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
	public void remove(IMediaFileTag tag) {
		synchronized (this) {
			Set<IMediaFileTag> set = new LinkedHashSet<>(getAll());
			int size = set.size();
			set.remove(tag);
			if (set.size() < size) {
				setAll(set);
			}
		}
	}

	@Override
	public void remove(int fileId, String tagName) {
		synchronized (this) {
			Set<IMediaFileTag> tags = fileIdCache.get(fileId);
			for (IMediaFileTag tag : tags) {
				if (tag.getTag().getName().equals(tagName)) {
					remove(tag);
					return;
				}
			}
		}
	}

	@Override
	public void add(int fileId, String tagName) {
		synchronized (this) {

			// Only add if does not already exist
			Set<IMediaFileTag> tags = fileIdCache.get(fileId);
			for (IMediaFileTag tag : tags) {
				if (tag.getTag().getName().equals(tagName)) {
					return;
				}
			}

			ITag tag = tagCache.getTag(tagName);
			add(new MediaFileTag(fileId, tag));
		}
	}

	@Override
	public Set<ITag> getTags(int fileId) {
		Set<IMediaFileTag> tags;
		synchronized (this) {
			tags = fileIdCache.get(fileId);
		}
		Set<ITag> set = new TreeSet<>();
		for (IMediaFileTag tag : tags) {
			set.add(tag.getTag());
		}
		return set;
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

		int fileId = Integer.parseInt(elements.get(0));
		String name = elements.get(1);
		ITag tag = tagCache.getTag(name);
		return new MediaFileTag(fileId, tag);
	}

	@Override
	public String formatToLine(IMediaFileTag tag) {
		return tag.getFileId() + "," + tag.getTag().getName();
	}

	@Override
	public void removeAll(int fileId) {
		synchronized (this) {
			Set<IMediaFileTag> set = new LinkedHashSet<>(getAll());
			boolean modified = false;

			Iterator<IMediaFileTag> iterator = set.iterator();
			while (iterator.hasNext()) {
				IMediaFileTag tag = iterator.next();
				if (tag.getFileId() == fileId) {
					log.info("[Remove] id={}, name={}", tag.getFileId(), tag.getTag().getName());
					iterator.remove();
					modified = true;
				}
			}
			if (modified) {
				setAll(set);
			}
		}
	}

}
