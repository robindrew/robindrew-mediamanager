package com.robindrew.mediamanager.files.media.tag;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import com.robindrew.common.io.Files;

public class TagCacheFile implements ITagCache {

	private static final MapSplitter splitter = Splitter.onPattern("\r\n").omitEmptyStrings().trimResults().withKeyValueSeparator(",");
	private static final MapJoiner joiner = Joiner.on("\r\n").withKeyValueSeparator(",");

	private final File file;
	private final AtomicInteger next = new AtomicInteger(1);
	private final ConcurrentMap<String, ITag> tagToNumberMap = new ConcurrentHashMap<>();
	private final ConcurrentMap<Integer, ITag> numberToTagMap = new ConcurrentHashMap<>();

	public TagCacheFile(File file) {
		this.file = file;

		readFromFile();
	}

	public ITag getTag(int number) {
		ITag tag = numberToTagMap.get(number);
		if (tag == null) {
			throw new IllegalArgumentException("tag number not found: " + number);
		}
		return tag;
	}

	private void readFromFile() {
		if (file.exists()) {
			String lines = Files.readToString(file);
			Map<String, String> map = splitter.split(lines);
			for (Entry<String, String> entry : map.entrySet()) {
				ITag tag = new Tag(Integer.parseInt(entry.getKey()), entry.getValue());
				cache(tag);
			}
		}
	}

	private void cache(ITag tag) {
		tagToNumberMap.put(tag.getName(), tag);
		numberToTagMap.put(tag.getNumber(), tag);

		int number = tag.getNumber();
		if (next.get() <= number) {
			next.set(number + 1);
		}
	}

	private void writeToFile() {
		String lines = joiner.join(numberToTagMap);
		Files.writeFromString(file, lines);
	}

	@Override
	public Set<ITag> getTags() {
		return new TreeSet<>(tagToNumberMap.values());
	}

	@Override
	public ITag getTag(String name) {
		ITag tag = tagToNumberMap.get(name);
		if (tag == null) {
			synchronized (this) {
				int number = next.getAndIncrement();
				tag = new Tag(number, name);
				tagToNumberMap.put(tag.getName(), tag);
				numberToTagMap.put(tag.getNumber(), tag);
				writeToFile();
			}
		}
		return tag;
	}
}
