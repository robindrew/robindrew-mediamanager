package com.robindrew.mediamanager.component.file.loader;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;

public class ImageKey {

	private final int id;
	private final int width;
	private final int height;
	private final BigDecimal seconds;
	private final BigDecimal duration;

	public ImageKey(int id, int width, int height, BigDecimal seconds, BigDecimal duration) {
		this.id = id;
		this.width = width;
		this.height = height;
		this.seconds = seconds;
		this.duration = duration;
	}

	public ImageKey(int id, int width, int height) {
		this(id, width, height, ZERO, ZERO);
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

	public BigDecimal getSeconds() {
		return seconds;
	}

	public BigDecimal getDuration() {
		return duration;
	}

	public boolean hasSeconds() {
		return !seconds.equals(ZERO);
	}

	public boolean hasDuration() {
		return !duration.equals(ZERO);
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
			if (this.getId() != that.getId()) {
				return false;
			}
			if (!this.getSeconds().equals(that.getSeconds())) {
				return false;
			}
			return this.getDuration().equals(that.getDuration());
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append(id).append("[").append(width).append("x").append(height);
		if (hasSeconds()) {
			text.append("@").append(seconds);
		}
		if (hasDuration()) {
			text.append("-").append(duration);
		}
		return text.toString();
	}

}
