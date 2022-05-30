package com.robindrew.mediamanager.jetty.page;

import static com.robindrew.common.dependency.DependencyFactory.getDependency;
import static com.robindrew.mediamanager.files.media.MediaType.VIDEO;

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
import com.robindrew.mediamanager.files.media.tag.ITag;
import com.robindrew.mediamanager.files.media.tag.ITagCache;
import com.robindrew.mediamanager.files.media.tag.file.IMediaFileTag;
import com.robindrew.mediamanager.files.media.tag.file.IMediaFileTagCache;
import com.robindrew.mediamanager.jetty.page.action.ModifyTagAction;
import com.robindrew.mediamanager.jetty.page.view.MediaFileTagView;

public class VideoTagPage extends AbstractServicePage {

	private static final IntegerProperty defaultVideosPerPage = new IntegerProperty("videos.per.page").defaultValue(9);

	public VideoTagPage(IVelocityHttpContext context, String templateName) {
		super(context, templateName);
	}

	@Override
	protected void execute(IHttpRequest request, IHttpResponse response, Map<String, Object> dataMap) {
		super.execute(request, response, dataMap);

		int tagNumber = request.getInteger("tagNumber");
		int pageNumber = request.getInteger("number", 1);
		int pageSize = request.getInteger("size", defaultVideosPerPage.get());
		int tagId = request.getInteger("tagId", -1);
		String tags = request.getString("tag", null);

		new ModifyTagAction().execute(tags, tagId);

		ITagCache tagCache = getDependency(ITagCache.class);
		ITag tag = tagCache.getTag(tagNumber);

		IMediaFileTagCache cache = getDependency(IMediaFileTagCache.class);
		Set<IMediaFileTag> fileTags = cache.getFileTags(tag);

		IFileManager manager = getDependency(IFileManager.class);
		Set<MediaFileTagView> views = MediaFileTagView.from(manager, fileTags, VIDEO);

		IPaginator<MediaFileTagView> paginator = new Paginator<>(views);
		List<MediaFileTagView> page = paginator.getPage(pageNumber, pageSize);
		List<MediaFileTagView> next = paginator.getPage(pageNumber + 1, pageSize);
		int pageCount = paginator.getPageCount(pageSize);

		dataMap.put("tag", tag);
		dataMap.put("tags", views);
		dataMap.put("page", page);
		dataMap.put("pageSize", pageSize);
		dataMap.put("previousPage", pageNumber - 1);
		dataMap.put("currentPage", pageNumber);
		dataMap.put("nextPage", next.isEmpty() ? 0 : pageNumber + 1);
		dataMap.put("pageCount", pageCount);
	}
}
