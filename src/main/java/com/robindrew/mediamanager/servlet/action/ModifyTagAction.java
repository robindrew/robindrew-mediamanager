package com.robindrew.mediamanager.servlet.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.robindrew.mediamanager.component.file.tagcache.IMediaFileTagCache;

public class ModifyTagAction {

	private static final Logger log = LoggerFactory.getLogger(ModifyTagAction.class);

	private static final Splitter splitter = Splitter.on(',').omitEmptyStrings().trimResults();

	private final IMediaFileTagCache cache;
	
	public ModifyTagAction(IMediaFileTagCache cache) {
		this.cache = cache;
	}
	
	public void execute(String tags, int fileId) {
		if (tags == null || tags.trim().isEmpty()) {
			return;
		}
		if (fileId < 0) {
			return;
		}
		tags = tags.trim();

		// Remove all Tags?
		if (tags.equals("--")) {

			log.info("[Remove All Tags] #{}", fileId);
			cache.removeAll(fileId);
			return;
		}

		// Add/Remove Tags
		for (String tag : splitter.split(tags)) {

			// Remove Tag
			if (tag.startsWith("-")) {
				tag = tag.substring(1);

				log.info("[Remove Tag] #{} -> '{}'", fileId, tags);
				cache.remove(fileId, tag);
			}

			// Add Tag
			else {
				// The + is optional
				if (tag.startsWith("+")) {
					tag = tag.substring(1);
				}

				log.info("[Add Tag] #{} -> '{}'", fileId, tag);
				cache.add(fileId, tag);
			}
		}
	}

}
