package com.robindrew.mediamanager.jetty.page;

import static com.robindrew.common.dependency.DependencyFactory.getDependency;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.robindrew.mediamanager.jetty.page.view.MediaFileTagView;

public class PhotoTagPage extends AbstractServicePage {

	private static final Logger log = LoggerFactory.getLogger(PhotoTagPage.class);
	
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

		IMediaFileTagCache cache = getDependency(IMediaFileTagCache.class);
		Set<IMediaFileTag> tags = cache.getTags(name);
		
		IFileManager manager = getDependency(IFileManager.class);
		Set<MediaFileTagView> views = MediaFileTagView.from(manager, tags);

		IPaginator<MediaFileTagView> paginator = new Paginator<>(views);
		List<MediaFileTagView> page = paginator.getPage(pageNumber, pageSize);
		List<MediaFileTagView> next = paginator.getPage(pageNumber + 1, pageSize);
		int pageCount = paginator.getPageCount(pageSize);

		log.info("Tag: " + name);
		for (IMediaFileTag file : tags) {
			log.info("Tag: " + file);
		}

		dataMap.put("name", name);
		dataMap.put("tags", views);
		dataMap.put("page", page);
		dataMap.put("previousPage", pageNumber - 1);
		dataMap.put("currentPage", pageNumber);
		dataMap.put("nextPage", next.isEmpty() ? 0 : pageNumber + 1);
		dataMap.put("pageCount", pageCount);
	}
}
