package com.robindrew.mediamanager.files.media.tag;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import com.robindrew.common.io.Files;

public class TagNumberFile {

	private static final MapSplitter splitter = Splitter.onPattern("\r\n").omitEmptyStrings().trimResults().withKeyValueSeparator(",");
	private static final MapJoiner joiner = Joiner.on("\r\n").withKeyValueSeparator(",");

	private final File file;
	private final AtomicInteger next = new AtomicInteger(1);
	private final ConcurrentMap<String, Integer> tagToNumberMap = new ConcurrentHashMap<>();
	private final ConcurrentMap<Integer, String> numberToTagMap = new ConcurrentHashMap<>();

	public TagNumberFile(File file) {
		this.file = file;

		readFromFile();
	}

	public String getTag(int number) {
		String tag = numberToTagMap.get(number);
		if (tag == null) {
			throw new IllegalArgumentException("tag number not found: " + number);
		}
		return tag;
	}

	public int getNumber(String tag) {
		Integer number = tagToNumberMap.get(tag);
		if (number == null) {
			synchronized (this) {
				number = next.getAndIncrement();
				tagToNumberMap.put(tag, number);
				numberToTagMap.put(number, tag);
				writeToFile();
			}
		}
		return number;
	}

	private void readFromFile() {
		if (file.exists()) {
			String lines = Files.readToString(file);
			Map<String, String> map = splitter.split(lines);
			for (Entry<String, String> entry : map.entrySet()) {
				setNumber(Integer.parseInt(entry.getKey()), entry.getValue());
			}
		}
	}

	private void setNumber(int number, String tag) {
		tagToNumberMap.put(tag, number);
		numberToTagMap.put(number, tag);
		if (next.get() <= number) {
			next.set(number + 1);
		}
	}

	private void writeToFile() {
		String lines = joiner.join(numberToTagMap);
		Files.writeFromString(file, lines);
	}

}
