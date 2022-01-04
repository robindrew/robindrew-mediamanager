package com.robindrew.mediamanager.jetty.page;

import static com.robindrew.common.dependency.DependencyFactory.getDependency;
import static com.robindrew.mediamanager.files.media.MediaType.PHOTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Splitter;
import com.robindrew.common.collect.IPaginator;
import com.robindrew.common.collect.Paginator;
import com.robindrew.common.http.servlet.executor.IVelocityHttpContext;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.http.servlet.response.IHttpResponse;
import com.robindrew.common.properties.map.type.IntegerProperty;
import com.robindrew.common.service.component.jetty.handler.page.AbstractServicePage;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;
import com.robindrew.mediamanager.files.media.IMediaFileCollection;
import com.robindrew.mediamanager.files.media.MediaFileCollection;
import com.robindrew.mediamanager.files.media.tag.IMediaFileTagCache;
import com.robindrew.mediamanager.files.media.tag.MediaFileTag;
import com.robindrew.mediamanager.jetty.page.action.ModifyTagAction;

public class PhotoCollectionPage extends AbstractServicePage {

	private static final IntegerProperty defaultPhotosPerPage = new IntegerProperty("photos.per.page").defaultValue(6);

	public PhotoCollectionPage(IVelocityHttpContext context, String templateName) {
		super(context, templateName);
	}

	@Override
	protected void execute(IHttpRequest request, IHttpResponse response, Map<String, Object> dataMap) {
		super.execute(request, response, dataMap);

		String name = request.getString("name");
		String type = request.getString("type", "name");
		int pageNumber = request.getInteger("number", 1);
		int pageSize = request.getInteger("size", defaultPhotosPerPage.get());
		String tags = request.getString("tag", null);
		int tagId = request.getInteger("tagId", -1);
		String allTags = request.getString("allTags", null);

		new ModifyTagAction().execute(tags, tagId);

		IFileManager manager = getDependency(IFileManager.class);
		Set<IMediaFile> files = manager.getMediaFiles();
		List<IMediaFileCollection> collections = MediaFileCollection.splitToListWithType(PHOTO, files);

		int index = indexOf(collections, name, type);
		IMediaFileCollection collection = collections.get(index);
		files = collection.getFiles();

		String nextName = (index == (collections.size() - 1)) ? name : collections.get(index + 1).getName();
		String prevName = (index == 0) ? name : collections.get(index - 1).getName();

		// Tag All command
		if (allTags != null) {
			IMediaFileTagCache cache = getDependency(IMediaFileTagCache.class);
			for (String tag : Splitter.on(',').omitEmptyStrings().trimResults().split(allTags)) {
				for (IMediaFile file : files) {
					cache.add(new MediaFileTag(file.getId(), tag));
				}
			}
		}

		IPaginator<IMediaFile> paginator = new Paginator<>(files);
		List<IMediaFile> page = paginator.getPage(pageNumber, pageSize);
		List<IMediaFile> next = paginator.getPage(pageNumber + 1, pageSize);
		int pageCount = paginator.getPageCount(pageSize);

		dataMap.put("root", manager.getRootDirectory());
		dataMap.put("collection", collection);
		dataMap.put("page", page);
		dataMap.put("pageSize", pageSize);
		dataMap.put("previousPage", pageNumber - 1);
		dataMap.put("currentPage", pageNumber);
		dataMap.put("nextPage", next.isEmpty() ? 0 : pageNumber + 1);
		dataMap.put("pageCount", pageCount);
		dataMap.put("prevName", prevName);
		dataMap.put("nextName", nextName);
	}

	private int indexOf(List<IMediaFileCollection> collections, String name, String type) {
		if (type.equals("name")) {
			for (int i = 0; i < collections.size(); i++) {
				IMediaFileCollection collection = collections.get(i);
				if (collection.getName().equals(name)) {
					return i;
				}
			}
		}
		if (type.equals("id")) {
			int id = Integer.parseInt(name);
			for (int i = 0; i < collections.size(); i++) {
				IMediaFileCollection collection = collections.get(i);
				if (collection.contains(id)) {
					return i;
				}
			}
		}
		throw new IllegalArgumentException("name=" + name + ", type=" + type);
	}

}
