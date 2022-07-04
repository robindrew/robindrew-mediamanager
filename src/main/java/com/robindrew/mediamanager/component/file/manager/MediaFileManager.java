package com.robindrew.mediamanager.component.file.manager;

import static com.robindrew.common.base.Preconditions.existsDirectory;
import static java.util.concurrent.TimeUnit.HOURS;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.robindrew.common.base.Threads;
import com.robindrew.common.io.file.Files;
import com.robindrew.mediamanager.component.file.cache.IMediaFile;
import com.robindrew.mediamanager.component.file.cache.IMediaFileCache;
import com.robindrew.mediamanager.component.file.cache.MediaFileType;
import com.robindrew.mediamanager.component.file.manager.archive.FileArchive;
import com.robindrew.mediamanager.component.file.manager.archive.IArchivedFile;
import com.robindrew.mediamanager.component.file.manager.archive.IFileArchive;

@Component
public class MediaFileManager implements IMediaFileManager {

	private static final Logger log = LoggerFactory.getLogger(MediaFileManager.class);

	private final File rootDirectory;
	private final String rootDirectoryPath;
	private final IMediaFileCache cache;
	private final List<File> files = new ArrayList<>();

	public MediaFileManager(@Value("${root.directory}") File rootDirectory, @Autowired IMediaFileCache cache) {
		this.rootDirectory = existsDirectory("rootDirectory", rootDirectory);
		this.rootDirectoryPath = rootDirectory.getAbsolutePath();
		this.cache = cache;
	}

	@PostConstruct
	public void init() {
		log.info("Loading Media Files");
		Stopwatch timer = Stopwatch.createStarted();
		getMediaFiles();
		timer.stop();
		log.info("Loaded {} Media Files in {}", files.size(), timer);
	}

	@Override
	public File getRootDirectory() {
		return rootDirectory;
	}

	public List<File> getFiles(boolean refresh) {
		synchronized (files) {
			if (refresh || files.isEmpty()) {
				files.clear();
				files.addAll(Files.listFiles(rootDirectory, true));
			}
			return ImmutableList.copyOf(files);
		}
	}

	@Override
	public Set<IMediaFile> getMediaFiles() {

		// Shortcut to avoid loading files if already loaded
		boolean modified = false;
		for (File file : getFiles(false)) {
			String filename = file.getName();
			String path = getPath(file);

			Optional<MediaFileType> type = MediaFileType.parseMediaFileType(filename);
			if (!type.isPresent()) {
				continue;
			}

			// Bypass loading files again!
			if (!cache.containsPath(path)) {
				modified = true;
				break;
			}
		}
		if (!modified) {
			return cache.getAll();
		}

		ExecutorService service = Threads.newFixedThreadPool("ArchiveReader", 6);
		Stopwatch timer = Stopwatch.createStarted();

		for (File file : getFiles(false)) {
			String filename = file.getName();
			String path = getPath(file);

			// Bypass loading files again!
			if (cache.containsPath(path)) {
				continue;
			}

			// Standard media file type?
			Optional<MediaFileType> type = MediaFileType.parseMediaFileType(filename);
			if (type.isPresent()) {
				cache.getMediaFile(path, type.get());
				continue;
			}

			// Is the file an archive?
			Optional<IFileArchive> archive = FileArchive.getFileArchive(file);
			if (archive.isPresent()) {
				service.submit(new Runnable() {

					@Override
					public void run() {
						cacheArchive(archive.get());
					}

				});
			}
		}

		Threads.shutdownService(service, 1, HOURS);
		cache.persistAll();
		timer.stop();

		log.info("Files loaded in {}", timer);

		return cache.getAll();
	}

	private void cacheArchive(IFileArchive archive) {
		try {
			Set<IMediaFile> files = new LinkedHashSet<>();

			log.info("Reading Archive: {}", archive);
			Stopwatch timer = Stopwatch.createStarted();
			for (IArchivedFile archived : archive.getArchivedFiles()) {
				Optional<MediaFileType> type = MediaFileType.parseMediaFileType(archived.getName());
				if (type.isPresent()) {
					String path = getPath(archived.getPath());
					files.add(cache.getMediaFile(path, type.get()));
					continue;
				}
			}
			timer.stop();
			log.info("Read Archive: {} in {}", archive, timer);

		} catch (Exception e) {
			log.warn("Failed to read archive: " + archive, e);
		}
	}

	private String getPath(File file) {
		return getPath(file.getAbsolutePath());
	}

	private String getPath(String path) {
		if (!path.startsWith(rootDirectoryPath)) {
			throw new IllegalArgumentException("file is not in the root directory: " + path);
		}
		path = path.substring(rootDirectoryPath.length()).replace('\\', '/');
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}

	@Override
	public IMediaFile getMediaFile(int id) {
		Set<IMediaFile> files = getMediaFiles();
		for (IMediaFile file : files) {
			if (file.getId() == id) {
				return file;
			}
		}
		throw new IllegalArgumentException("no file found with id=" + id);
	}

}
