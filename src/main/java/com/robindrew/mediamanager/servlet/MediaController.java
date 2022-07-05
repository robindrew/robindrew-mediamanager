package com.robindrew.mediamanager.servlet;

import static com.robindrew.mediamanager.component.file.cache.MediaFileCollection.splitToListWithType;
import static com.robindrew.mediamanager.component.file.cache.MediaType.PHOTO;
import static com.robindrew.mediamanager.component.file.cache.MediaType.VIDEO;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.robindrew.mediamanager.component.file.cache.IMediaFile;
import com.robindrew.mediamanager.component.file.cache.IMediaFileCollection;
import com.robindrew.mediamanager.component.file.manager.IMediaFileManager;
import com.robindrew.mediamanager.component.file.tagcache.IMediaFileTagCache;
import com.robindrew.spring.component.servlet.template.VelocityDataMapper;

@Controller
public class MediaController {

	@Autowired
	private IMediaFileManager fileManager;
	@Autowired
	private IMediaFileTagCache fileTagCache;
	@Autowired
	private VelocityDataMapper mapper;

	@GetMapping("/Videos")
	public ModelMap videos() {

		Set<IMediaFile> files = fileManager.getMediaFiles();
		List<IMediaFileCollection> collections = splitToListWithType(VIDEO, files);

		ModelMap dataMap = mapper.newModelMap();
		dataMap.put("root", fileManager.getRootDirectory());
		dataMap.put("collections", collections);
		dataMap.put("tagCache", fileTagCache);
		return dataMap;
	}

	@GetMapping("/Photos")
	public ModelMap photos() {

		Set<IMediaFile> files = fileManager.getMediaFiles();
		List<IMediaFileCollection> collections = splitToListWithType(PHOTO, files);

		ModelMap dataMap = mapper.newModelMap();
		dataMap.put("root", fileManager.getRootDirectory());
		dataMap.put("collections", collections);
		dataMap.put("tagCache", fileTagCache);
		return dataMap;
	}
}
