package com.robindrew.mediamanager.files.media.tag;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.robindrew.common.text.Strings;
import com.robindrew.common.util.Check;

public class MediaFileTag implements IMediaFileTag {

	private final int id;
	private final String name;

	public MediaFileTag(int id, String name) {
		this.id = id;
		this.name = Check.notEmpty("name", name);
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode() * id;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof IMediaFileTag) {
			IMediaFileTag that = (IMediaFileTag) object;
			if (this.getId() != that.getId()) {
				return false;
			}
			return this.getName().equals(that.getName());
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
		compare.append(this.getId(), that.getId());
		compare.append(this.getName(), that.getName());
		return compare.toComparison();
	}

}
