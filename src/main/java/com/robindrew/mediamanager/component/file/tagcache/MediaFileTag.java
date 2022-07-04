package com.robindrew.mediamanager.component.file.tagcache;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.robindrew.common.text.Strings;
import com.robindrew.mediamanager.component.tag.ITag;

public class MediaFileTag implements IMediaFileTag {

	private final int fileId;
	private final ITag tag;

	public MediaFileTag(int fileId, ITag tag) {
		this.fileId = fileId;
		this.tag = tag;
	}

	@Override
	public int getFileId() {
		return fileId;
	}

	@Override
	public ITag getTag() {
		return tag;
	}

	@Override
	public int hashCode() {
		return tag.hashCode() * fileId;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof IMediaFileTag) {
			IMediaFileTag that = (IMediaFileTag) object;
			if (this.getFileId() != that.getFileId()) {
				return false;
			}
			return this.getTag().equals(that.getTag());
		}
		return false;
	}

	@Override
	public String toString() {
		return Strings.toString(this);
	}

	@Override
	public int compareTo(IMediaFileTag that) {
		CompareToBuilder compare = new CompareToBuilder();
		compare.append(this.getFileId(), that.getFileId());
		compare.append(this.getTag(), that.getTag());
		return compare.toComparison();
	}

}
