package com.robindrew.mediamanager.jetty.page;

import static com.robindrew.common.dependency.DependencyFactory.getDependency;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.robindrew.common.collect.IPaginator;
import com.robindrew.common.collect.Paginator;
import com.robindrew.common.http.servlet.executor.IVelocityHttpContext;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.http.servlet.response.IHttpResponse;
import com.robindrew.common.properties.map.type.IntegerProperty;
import com.robindrew.common.service.component.jetty.handler.page.AbstractServicePage;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.tag.IMediaFileTag;
import com.robindrew.mediamanager.files.media.tag.IMediaFileTagCache;
import com.robindrew.mediamanager.jetty.page.action.ModifyTagAction;
import com.robindrew.mediamanager.jetty.page.view.MediaFileTagView;

public class PhotoTagPage extends AbstractServicePage {

	private static final IntegerProperty defaultPhotosPerPage = new IntegerProperty("photos.per.page").defaultValue(6);

	public PhotoTagPage(IVelocityHttpContext context, String templateName) {
		super(context, templateName);
	}

	@Override
	protected void execute(IHttpRequest request, IHttpResponse response, Map<String, Object> dataMap) {
		super.execute(request, response, dataMap);

		String name = request.getString("name");
		int pageNumber = request.getInteger("number", 1);
		int pageSize = request.getInteger("size", defaultPhotosPerPage.get());
		int tagId = request.getInteger("tagId", -1);
		String tags = request.getString("tag", null);

		new ModifyTagAction().execute(tags, tagId);

		IMediaFileTagCache cache = getDependency(IMediaFileTagCache.class);
		Set<IMediaFileTag> tagSet = cache.getTags(name);

		IFileManager manager = getDependency(IFileManager.class);
		Set<MediaFileTagView> views = MediaFileTagView.from(manager, tagSet);

		IPaginator<MediaFileTagView> paginator = new Paginator<>(views);
		List<MediaFileTagView> page = paginator.getPage(pageNumber, pageSize);
		List<MediaFileTagView> next = paginator.getPage(pageNumber + 1, pageSize);
		int pageCount = paginator.getPageCount(pageSize);

		dataMap.put("name", name);
		dataMap.put("tags", views);
		dataMap.put("page", page);
		dataMap.put("pageSize", pageSize);
		dataMap.put("previousPage", pageNumber - 1);
		dataMap.put("currentPage", pageNumber);
		dataMap.put("nextPage", next.isEmpty() ? 0 : pageNumber + 1);
		dataMap.put("pageCount", pageCount);
	}
}
