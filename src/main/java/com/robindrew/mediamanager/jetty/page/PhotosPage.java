package com.robindrew.mediamanager.jetty.page;

import static com.robindrew.common.dependency.DependencyFactory.getDependency;
import static com.robindrew.mediamanager.files.media.MediaType.PHOTO;

import java.util.Map;
import java.util.Set;

import com.robindrew.common.http.servlet.executor.IVelocityHttpContext;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.http.servlet.response.IHttpResponse;
import com.robindrew.common.service.component.jetty.handler.page.AbstractServicePage;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;
import com.robindrew.mediamanager.files.media.IMediaFileCollection;
import com.robindrew.mediamanager.files.media.MediaFileCollection;
import com.robindrew.mediamanager.files.media.tag.IMediaFileTagCache;

public class PhotosPage extends AbstractServicePage {

	public PhotosPage(IVelocityHttpContext context, String templateName) {
		super(context, templateName);
	}

	@Override
	protected void execute(IHttpRequest request, IHttpResponse response, Map<String, Object> dataMap) {
		super.execute(request, response, dataMap);

		IFileManager manager = getDependency(IFileManager.class);
		Set<IMediaFile> files = manager.getMediaFiles();
		Set<IMediaFileCollection> collections = MediaFileCollection.splitToSetWithType(PHOTO, files);

		IMediaFileTagCache tagCache = getDependency(IMediaFileTagCache.class);

		dataMap.put("root", manager.getRootDirectory());
		dataMap.put("collections", collections);
		dataMap.put("tagCache", tagCache);
	}

}
