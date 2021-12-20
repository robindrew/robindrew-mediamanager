package com.robindrew.mediamanager.jetty.page;

import static com.robindrew.common.dependency.DependencyFactory.getDependency;
import static com.robindrew.mediamanager.files.media.MediaType.VIDEO;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.robindrew.common.collect.IPaginator;
import com.robindrew.common.collect.Paginator;
import com.robindrew.common.http.servlet.executor.IVelocityHttpContext;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.http.servlet.response.IHttpResponse;
import com.robindrew.common.service.component.jetty.handler.page.AbstractServicePage;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;
import com.robindrew.mediamanager.files.media.IMediaFileCollection;
import com.robindrew.mediamanager.files.media.MediaFileCollection;
import com.robindrew.mediamanager.files.media.tag.IMediaFileTagCache;
import com.robindrew.mediamanager.files.media.tag.MediaFileTag;

public class VideoCollectionPage extends AbstractServicePage {

	private static final Logger log = LoggerFactory.getLogger(VideoCollectionPage.class);

	private static final int DEFAULT_PHOTOS_PER_PAGE = 6;

	public VideoCollectionPage(IVelocityHttpContext context, String templateName) {
		super(context, templateName);
	}

	@Override
	protected void execute(IHttpRequest request, IHttpResponse response, Map<String, Object> dataMap) {
		super.execute(request, response, dataMap);

		String name = request.getString("name");
		String type = request.getString("type", "name");
		int pageNumber = request.getInteger("number", 1);
		int pageSize = request.getInteger("size", DEFAULT_PHOTOS_PER_PAGE);
		String tags = request.getString("tag", null);
		int tagId = request.getInteger("tagId", -1);

		if (tags != null && tagId >= 0) {
			log.info("[Add Tag] #{} -> '{}'", tagId, tags);

			IMediaFileTagCache cache = getDependency(IMediaFileTagCache.class);
			for (String tag : Splitter.on(',').omitEmptyStrings().trimResults().split(tags)) {
				cache.add(new MediaFileTag(tagId, tag));
			}
		}

		IFileManager manager = getDependency(IFileManager.class);
		Set<IMediaFile> files = manager.getMediaFiles();
		IMediaFileCollection collection = getCollection(files, name, type);
		files = collection.getFiles();

		IPaginator<IMediaFile> paginator = new Paginator<>(files);
		List<IMediaFile> page = paginator.getPage(pageNumber, pageSize);
		List<IMediaFile> next = paginator.getPage(pageNumber + 1, pageSize);
		int pageCount = paginator.getPageCount(pageSize);

		dataMap.put("root", manager.getRootDirectory());
		dataMap.put("collection", collection);
		dataMap.put("page", page);
		dataMap.put("previousPage", pageNumber - 1);
		dataMap.put("currentPage", pageNumber);
		dataMap.put("nextPage", next.isEmpty() ? 0 : pageNumber + 1);
		dataMap.put("pageCount", pageCount);
	}

	private IMediaFileCollection getCollection(Set<IMediaFile> files, String name, String type) {
		Set<IMediaFileCollection> collections = MediaFileCollection.splitToSetWithType(VIDEO, files);
		if (type.equals("name")) {
			for (IMediaFileCollection collection : collections) {
				if (collection.getName().equals(name)) {
					return collection;
				}
			}
		}
		if (type.equals("id")) {
			int id = Integer.parseInt(name);
			for (IMediaFileCollection collection : collections) {
				if (collection.contains(id)) {
					return collection;
				}
			}
		}
		throw new IllegalArgumentException("name=" + name);
	}

}
