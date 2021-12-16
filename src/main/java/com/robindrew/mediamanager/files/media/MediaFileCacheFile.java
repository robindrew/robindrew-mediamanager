package com.robindrew.mediamanager.files.media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.LineProcessor;
import com.robindrew.common.io.Files;
import com.robindrew.common.util.Check;

public class MediaFileCacheFile implements IMediaFileCache {

	public static final String normalizePath(String path) {
		return path.replace('\\', '/');
	}

	private static final Logger log = LoggerFactory.getLogger(MediaFileCacheFile.class);

	private final File cacheFile;
	private final AtomicInteger nextId = new AtomicInteger(0);
	private final Map<Integer, IMediaFile> idToFileMap = new HashMap<>();
	private final Map<String, IMediaFile> pathToFileMap = new TreeMap<>();
	private final Set<String> sourcePathSet = new TreeSet<>();

	public MediaFileCacheFile(File cacheFile) {
		this.cacheFile = Check.notNull("cacheFile", cacheFile);

		readCacheFile();
	}

	@Override
	public IMediaFile getMediaFile(String path, MediaFileType type) {
		path = normalizePath(path);
		synchronized (cacheFile) {
			IMediaFile file = pathToFileMap.get(path);
			if (file == null) {
				int id = nextId.getAndIncrement();
				file = new MediaFile(id, path, type);
				cache(file);
			}
			return file;
		}
	}

	private void cache(IMediaFile file) {
		synchronized (cacheFile) {
			idToFileMap.put(file.getId(), file);
			pathToFileMap.put(file.getPath(), file);
			sourcePathSet.add(file.getSourcePath());
		}
	}

	@Override
	public boolean containsPath(String path) {
		path = normalizePath(path);
		synchronized (cacheFile) {
			return sourcePathSet.contains(path);
		}
	}

	@Override
	public Set<IMediaFile> getAll() {
		synchronized (cacheFile) {
			return ImmutableSet.copyOf(pathToFileMap.values());
		}
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int size() {
		synchronized (cacheFile) {
			return idToFileMap.size();
		}
	}

	@Override
	public void persistAll() {
		synchronized (cacheFile) {
			if (isEmpty()) {
				deleteCacheFile();
			} else {
				writeCacheFile();
			}
		}
	}

	private void deleteCacheFile() {
		if (cacheFile.exists()) {
			cacheFile.delete();
		}
	}

	private void readCacheFile() {
		if (cacheFile.exists()) {
			log.info("Reading cache file '{}'", cacheFile);
			Stopwatch timer = Stopwatch.createStarted();
			Files.readFromLines(cacheFile, new MediaFileProcessor());
			timer.stop();
			log.info("Read cache file: {} files in {}", size(), timer);
		}
	}

	private void writeCacheFile() {
		if (!isEmpty()) {
			log.info("Writing cache file '{}'", cacheFile);
			Stopwatch timer = Stopwatch.createStarted();
			Files.writeFromLines(cacheFile, new MediaFileIterable());
			timer.stop();
			log.info("Written cache file in {}", timer);
		}
	}

	private class MediaFileIterable implements Iterable<String> {

		@Override
		public Iterator<String> iterator() {
			synchronized (cacheFile) {
				Iterator<IMediaFile> iterator = new ArrayList<>(idToFileMap.values()).iterator();
				return new MediaFileIterator(iterator);
			}
		}
	}

	private class MediaFileIterator implements Iterator<String> {

		private final Iterator<IMediaFile> iterator;

		private MediaFileIterator(Iterator<IMediaFile> iterator) {
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public String next() {
			IMediaFile file = iterator.next();
			return file.getId() + "," + file.getType() + "," + file.getPath();
		}
	}

	private class MediaFileProcessor implements LineProcessor<String> {

		@Override
		public boolean processLine(String line) throws IOException {

			// Parse the id
			int comma1 = line.indexOf(',');
			int id = Integer.parseInt(line.substring(0, comma1));

			// Parse the type
			int comma2 = line.indexOf(',', comma1 + 1);
			MediaFileType type = MediaFileType.valueOf(line.substring(comma1 + 1, comma2));

			// Parse the path
			String path = line.substring(comma2 + 1);

			IMediaFile file = new MediaFile(id, path, type);
			cache(file);
			if (nextId.get() <= id) {
				nextId.set(id + 1);
			}

			return true;
		}

		@Override
		public String getResult() {
			return null;
		}

	}

}
