package com.robindrew.mediamanager.files.media.tag;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class Tag implements ITag {

	private final int number;
	private final String name;

	public Tag(int number, String name) {
		if (number < 0) {
			throw new IllegalArgumentException("number=" + number);
		}
		name = validate(name);
		if (name.isEmpty()) {
			throw new IllegalArgumentException("name=" + name);
		}
		this.number = number;
		this.name = name;
	}

	private static String validate(String name) {
		name = name.trim();
		try {
			Integer.parseInt(name);
		} catch (Exception e) {
			return name;
		}
		throw new IllegalArgumentException("name is a number");
	}

	@Override
	public int getNumber() {
		return number;
	}

	@Override
	public String getName() {
		return name;
	}

	public int hashCode() {
		return number * 1999;
	}

	public boolean equals(Object object) {
		if (object instanceof ITag) {
			return this.getNumber() == ((ITag) object).getNumber();
		}
		return false;
	}

	@Override
	public int compareTo(ITag that) {
		CompareToBuilder compare = new CompareToBuilder();
		compare.append(this.getName(), that.getName());
		return compare.toComparison();
	}

	@Override
	public String toString() {
		return name;
	}

}
