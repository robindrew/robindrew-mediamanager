package com.robindrew.mediamanager.component.file.loader;

public class ImageKey {

	private final int id;
	private final int width;
	private final int height;

	public ImageKey(int id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
	}

	public int getId() {
		return id;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public int hashCode() {
		return (id + width + height) * 1999;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof ImageKey) {
			ImageKey that = (ImageKey) object;
			if (this.getWidth() != that.getWidth()) {
				return false;
			}
			if (this.getHeight() != that.getHeight()) {
				return false;
			}
			return this.getId() == that.getId();
		}
		return false;
	}

	@Override
	public String toString() {
		return id + "[" + width + "x" + height + "]";
	}

}
