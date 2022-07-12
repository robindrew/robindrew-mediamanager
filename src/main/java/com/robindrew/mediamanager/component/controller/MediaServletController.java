package com.robindrew.mediamanager.component.controller;

import static com.robindrew.mediamanager.component.file.cache.MediaFileCollection.splitToListWithType;
import static com.robindrew.mediamanager.component.file.cache.MediaType.PHOTO;
import static com.robindrew.mediamanager.component.file.cache.MediaType.VIDEO;
import static org.springframework.http.MediaType.parseMediaType;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.robindrew.common.http.MimeType;
import com.robindrew.common.io.file.Files;
import com.robindrew.mediamanager.component.file.cache.IMediaFile;
import com.robindrew.mediamanager.component.file.cache.IMediaFileCollection;
import com.robindrew.mediamanager.component.file.manager.IMediaFileManager;
import com.robindrew.mediamanager.component.file.tagcache.IMediaFileTagCache;
import com.robindrew.mediamanager.component.tag.ITag;
import com.robindrew.spring.component.servlet.template.VelocityDataMapper;

@Controller
public class MediaServletController {

	@Autowired
	private IMediaFileManager fileManager;
	@Autowired
	private IMediaFileTagCache fileTagCache;
	@Autowired
	private VelocityDataMapper mapper;

	@GetMapping("/Videos")
	public ModelMap videos() {

		Set<IMediaFile> files = fileManager.getMediaFiles();
		Set<ITag> tags = new TreeSet<>();
		List<IMediaFileCollection> collections = splitToListWithType(VIDEO, files, tags);

		ModelMap dataMap = mapper.newModelMap();
		dataMap.put("root", fileManager.getRootDirectory());
		dataMap.put("collections", collections);
		dataMap.put("tagCache", fileTagCache);
		dataMap.put("tags", tags);
		return dataMap;
	}

	@GetMapping("/Photos")
	public ModelMap photos() {

		Set<IMediaFile> files = fileManager.getMediaFiles();
		Set<ITag> tags = new TreeSet<>();
		List<IMediaFileCollection> collections = splitToListWithType(PHOTO, files, tags);

		ModelMap dataMap = mapper.newModelMap();
		dataMap.put("root", fileManager.getRootDirectory());
		dataMap.put("collections", collections);
		dataMap.put("tagCache", fileTagCache);
		dataMap.put("tags", tags);
		return dataMap;
	}

	@GetMapping("/Videos/ViewVideo")
	public ResponseEntity<StreamingResponseBody> viewVideo(@RequestParam int id) {

		IMediaFile mediaFile = fileManager.getMediaFile(id);
		File file = new File(fileManager.getRootDirectory(), mediaFile.getSourcePath());
		ByteSource video = Files.asByteSource(file);
		String type = MimeType.forExtension(mediaFile.getName());

		// Streaming response
		StreamingResponseBody responseBody = response -> {
			try (InputStream from = video.openBufferedStream()) {
				ByteStreams.copy(from, response);
			} catch (Exception e) {
			}
		};

		return ResponseEntity.ok().contentType(parseMediaType(type)).body(responseBody);
	}
}
