package com.robindrew.mediamanager.files.media;

import java.util.Set;

import com.robindrew.common.dependency.DependencyFactory;
import com.robindrew.common.util.Check;
import com.robindrew.mediamanager.files.media.tag.IMediaFileTagCache;

public class MediaFile implements IMediaFile {

	private final int id;
	private final String path;
	private final MediaFileType type;

	public MediaFile(int id, String path, MediaFileType type) {
		this.id = id;
		this.path = Check.notEmpty("path", path);
		this.type = Check.notNull("type", type);
	}

	@Override
	public String getSourcePath() {
		int hashIndex = path.lastIndexOf(".zip#");
		if (hashIndex != -1) {
			return path.substring(0, hashIndex + 4);
		}
		return path;
	}

	@Override
	public boolean isArchived() {
		int hashIndex = path.lastIndexOf(".zip#");
		return hashIndex != -1;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public MediaFileType getType() {
		return type;
	}

	@Override
	public String toString() {
		return path.toString();
	}

	@Override
	public int hashCode() {
		return path.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof IMediaFile) {
			IMediaFile that = (IMediaFile) object;
			return this.getPath().equals(that.getPath());
		}
		return false;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {

		// Archive
		int hashIndex = path.indexOf('#');
		if (hashIndex != -1) {
			return path.substring(hashIndex + 1);
		}

		// Directory
		int slashIndex = path.lastIndexOf('/');
		if (slashIndex != -1) {
			return path.substring(slashIndex + 1);
		}

		// None??
		return path;
	}

	@Override
	public Set<String> getTags() {
		IMediaFileTagCache cache = DependencyFactory.getDependency(IMediaFileTagCache.class);
		return cache.getTagNames(getId());
	}

}
