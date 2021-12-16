package com.robindrew.mediamanager.jetty.page.view;

import java.util.LinkedHashSet;
import java.util.Set;

import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;
import com.robindrew.mediamanager.files.media.MediaFileType;
import com.robindrew.mediamanager.files.media.tag.IMediaFileTag;

public class MediaFileTagView {

	public static Set<MediaFileTagView> from(IFileManager manager, Set<IMediaFileTag> tags) {
		Set<MediaFileTagView> set = new LinkedHashSet<>();
		for (IMediaFileTag tag : tags) {
			IMediaFile file = manager.getMediaFile(tag.getId());
			set.add(new MediaFileTagView(tag, file));	
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
		return tag.getId();
	}

	public String getName() {
		return tag.getName();
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
