package com.robindrew.mediamanager.component.controller;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.robindrew.mediamanager.component.file.tagcache.IMediaFileTagCache;
import com.robindrew.mediamanager.component.tag.ITag;

@RestController("/api/media/")
public class MediaRestController {

	private final IMediaFileTagCache tagCache;

	public MediaRestController(IMediaFileTagCache tagCache) {
		this.tagCache = tagCache;
	}

	@GetMapping("/v1/tags")
	public Set<ITag> getTags() {
		return tagCache.getTags();
	}

}
