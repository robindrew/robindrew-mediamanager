package com.robindrew.mediamanager.servlet.view;

import java.util.LinkedHashSet;
import java.util.Set;

import com.robindrew.mediamanager.component.file.cache.IMediaFile;
import com.robindrew.mediamanager.component.file.cache.MediaFileType;
import com.robindrew.mediamanager.component.file.cache.MediaType;
import com.robindrew.mediamanager.component.file.manager.IMediaFileManager;
import com.robindrew.mediamanager.component.file.tagcache.IMediaFileTag;

public class MediaFileTagView {

	public static Set<MediaFileTagView> from(IMediaFileManager manager, Set<IMediaFileTag> tags, MediaType type) {
		Set<MediaFileTagView> set = new LinkedHashSet<>();
		for (IMediaFileTag tag : tags) {
			IMediaFile file = manager.getMediaFile(tag.getFileId());
			if (file.getType().getType().equals(type)) {
				set.add(new MediaFileTagView(tag, file));
			}
		}
		return set;
	}

	private final IMediaFileTag tag;
	private final IMediaFile file;

	public MediaFileTagView(IMediaFileTag tag, IMediaFile file) {
		this.tag = tag;
		this.file = file;
	}

	public int getId() {
		return tag.getFileId();
	}

	public String getName() {
		return tag.getTag().getName();
	}

	public MediaFileType getType() {
		return file.getType();
	}

	public IMediaFileTag getTag() {
		return tag;
	}

	public IMediaFile getFile() {
		return file;
	}

}
