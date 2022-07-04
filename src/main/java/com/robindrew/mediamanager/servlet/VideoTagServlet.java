package com.robindrew.mediamanager.servlet;

import static com.robindrew.mediamanager.component.file.cache.MediaType.VIDEO;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.robindrew.common.collect.IPaginator;
import com.robindrew.common.collect.Paginator;
import com.robindrew.common.http.response.IHttpResponse;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.http.servlet.template.AbstractTemplateServlet;
import com.robindrew.common.http.servlet.template.TemplateResource;
import com.robindrew.mediamanager.component.file.manager.IMediaFileManager;
import com.robindrew.mediamanager.component.file.tagcache.IMediaFileTag;
import com.robindrew.mediamanager.component.file.tagcache.IMediaFileTagCache;
import com.robindrew.mediamanager.component.tag.ITag;
import com.robindrew.mediamanager.component.tag.ITagCache;
import com.robindrew.mediamanager.servlet.action.ModifyTagAction;
import com.robindrew.mediamanager.servlet.view.MediaFileTagView;

@WebServlet(urlPatterns = "/Videos/Tag")
@TemplateResource("site/media/videos/Tag.html")
public class VideoTagServlet extends AbstractTemplateServlet {

	@Value("${videos.per.page:9}")
	private int defaultVideosPerPage;

	@Autowired
	private ITagCache tagCache;
	@Autowired
	private IMediaFileTagCache fileTagCache;
	@Autowired
	private IMediaFileManager fileManager;
	
	@Override
	protected void execute(IHttpRequest request, IHttpResponse response, Map<String, Object> dataMap) {
		super.execute(request, response, dataMap);

		int tagNumber = request.getInteger("tagNumber");
		int pageNumber = request.getInteger("number", 1);
		int pageSize = request.getInteger("size", defaultVideosPerPage);
		int tagId = request.getInteger("tagId", -1);
		String tags = request.getString("tag", null);

		new ModifyTagAction(fileTagCache).execute(tags, tagId);

		ITag tag = tagCache.getTag(tagNumber);
		Set<IMediaFileTag> fileTags = fileTagCache.getFileTags(tag);
		Set<MediaFileTagView> views = MediaFileTagView.from(fileManager, fileTags, VIDEO);

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
