package com.robindrew.mediamanager.component.file.cache;

import static com.robindrew.mediamanager.component.file.cache.MediaType.PHOTO;
import static com.robindrew.mediamanager.component.file.cache.MediaType.VIDEO;

import java.util.Optional;

public enum MediaFileType {

	JPG(PHOTO), PNG(PHOTO), GIF(PHOTO), MP4(VIDEO), MPG(VIDEO), WMV(VIDEO), M4V(VIDEO), MOV(VIDEO);

	public static final Optional<MediaFileType> parseMediaFileType(String filename) {
		String lower = filename.toUpperCase();
		int dot = lower.lastIndexOf('.');
		if (dot == -1) {
			return Optional.empty();
		}
		String ext = lower.substring(dot + 1);
		for (MediaFileType type : values()) {
			if (type.name().equals(ext)) {
				return Optional.of(type);
			}
		}
		return Optional.empty();
	}

	private final MediaType type;

	private MediaFileType(MediaType type) {
		this.type = type;
	}

	public boolean isVideo() {
		return type.equals(VIDEO);
	}

	public boolean isPhoto() {
		return type.equals(PHOTO);
	}

	public MediaType getType() {
		return type;
	}

}
