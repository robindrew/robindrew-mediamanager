package com.robindrew.mediamanager;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.robindrew.mediamanager.component.tag.ITagCache;
import com.robindrew.mediamanager.component.tag.TagCacheFile;
import com.robindrew.mediamanager.component.file.cache.IMediaFileCache;
import com.robindrew.mediamanager.component.file.cache.MediaFileCacheFile;
import com.robindrew.mediamanager.component.file.loader.IMediaFileLoader;
import com.robindrew.mediamanager.component.file.loader.MediaFileLoader;
import com.robindrew.mediamanager.component.file.manager.IMediaFileManager;
import com.robindrew.mediamanager.component.file.manager.MediaFileManager;
import com.robindrew.mediamanager.component.file.tagcache.IMediaFileTagCache;
import com.robindrew.mediamanager.component.file.tagcache.MediaFileTagCacheFile;

@Configuration
public class MediaManagerConfiguration {

	@Bean
	public ITagCache getTagCache(@Value("${tag.cache.file}") String filename) {
		return new TagCacheFile(new File(filename));
	}

	@Bean
	public IMediaFileTagCache getMediaFileTagCache(@Autowired ITagCache tagCache, @Value("${media.tag.cache.file}") File file) {
		return new MediaFileTagCacheFile(tagCache, file);
	}

	@Bean
	public IMediaFileManager getFileManager(@Value("${root.directory}") File rootDirectory, @Autowired IMediaFileCache cache) {
		return new MediaFileManager(rootDirectory, cache);
	}

	@Bean
	public IMediaFileCache getMediaFileCache(@Value("${cache.file}") File file) {
		return new MediaFileCacheFile(file);
	}

	@Bean
	public IMediaFileLoader getMediaFileLoader(@Autowired IMediaFileManager manager, @Value("${cache.file.directory}") File cacheDirectory) {
		return new MediaFileLoader(manager, cacheDirectory);
	}

}