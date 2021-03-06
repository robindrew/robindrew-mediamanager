package com.robindrew.mediamanager.files;

import static com.robindrew.common.dependency.DependencyFactory.getDependency;
import static com.robindrew.common.dependency.DependencyFactory.setDependency;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.robindrew.common.mbean.IMBeanRegistry;
import com.robindrew.common.mbean.annotated.AnnotatedMBeanRegistry;
import com.robindrew.common.properties.map.type.IProperty;
import com.robindrew.common.properties.map.type.IntegerProperty;
import com.robindrew.common.properties.map.type.StringProperty;
import com.robindrew.common.service.component.AbstractIdleComponent;
import com.robindrew.common.util.Threads;
import com.robindrew.mediamanager.files.manager.FileManager;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;
import com.robindrew.mediamanager.files.media.IMediaFileCache;
import com.robindrew.mediamanager.files.media.MediaFileCacheFile;
import com.robindrew.mediamanager.files.media.MediaType;
import com.robindrew.mediamanager.files.media.loader.IMediaFileLoader;
import com.robindrew.mediamanager.files.media.loader.MediaFileLoader;
import com.robindrew.mediamanager.files.media.tag.IMediaFileTagCache;
import com.robindrew.mediamanager.files.media.tag.MediaFileTagCacheFile;

public class MediaFileComponent extends AbstractIdleComponent implements MediaFileComponentMBean {

	private static final Logger log = LoggerFactory.getLogger(MediaFileComponent.class);

	/** The property "root.directory". */
	private static final IProperty<String> rootDirectory = new StringProperty("root.directory");
	private static final IProperty<String> cacheFile = new StringProperty("cache.file");
	private static final IProperty<String> cacheFileDirectory = new StringProperty("cache.file.directory");
	private static final IProperty<String> tagCacheFile = new StringProperty("tag.cache.file");
	private static final IProperty<Integer> backgroundLoaderThreads = new IntegerProperty("background.loader.threads").defaultValue(2);

	public IFileManager getManager() {
		return getDependency(IFileManager.class);
	}

	@Override
	protected void startupComponent() throws Exception {
		log.info("Cache File: " + cacheFile.get());
		log.info("Cache Directory: " + cacheFileDirectory.get());
		log.info("Tag Cache File: " + tagCacheFile.get());

		// File Manager
		IMediaFileCache cache = new MediaFileCacheFile(new File(cacheFile.get()));
		IMediaFileTagCache tagCache = new MediaFileTagCacheFile(new File(tagCacheFile.get()));
		IFileManager manager = new FileManager(new File(rootDirectory.get()), cache);
		setDependency(IFileManager.class, manager);
		setDependency(IMediaFileTagCache.class, tagCache);

		IMediaFileLoader loader = new MediaFileLoader(manager, new File(cacheFileDirectory.get()));
		setDependency(IMediaFileLoader.class, loader);

		log.info("Loading Media Files in {} ...", cacheFileDirectory.get());
		Stopwatch timer = Stopwatch.createStarted();
		Set<IMediaFile> files = getManager().getMediaFiles();
		timer.stop();
		log.info("Loaded {} Media Files in {}", files.size(), timer);

		// Asynchronously load all files
		int threads = backgroundLoaderThreads.get();
		if (threads > 0) {
			loadImagesInBackground(loader, files, threads);
		}

		// MBean
		IMBeanRegistry registry = new AnnotatedMBeanRegistry();
		registry.register(this);
	}

	private void loadImagesInBackground(IMediaFileLoader loader, Set<IMediaFile> files, int threads) {
		ExecutorService pool = Threads.newFixedThreadPool("BackgroundImageLoader", threads);
		for (IMediaFile file : files) {
			if (file.getType().getType().equals(MediaType.PHOTO)) {
				pool.submit(new Runnable() {

					@Override
					public void run() {
						loader.getImage(file, 320, 240, true);
					}
				});
			}
		}
	}

	@Override
	protected void shutdownComponent() throws Exception {
	}

	@Override
	public String getRootDirectory() {
		return getManager().getRootDirectory().getAbsolutePath();
	}

	@Override
	public Set<String> getMediaFiles() {
		Set<String> set = new TreeSet<>();
		for (IMediaFile file : getManager().getMediaFiles()) {
			set.add(file.getPath());
		}
		return set;
	}

	@Override
	public int getMediaFileCount() {
		return getMediaFiles().size();
	}

}
