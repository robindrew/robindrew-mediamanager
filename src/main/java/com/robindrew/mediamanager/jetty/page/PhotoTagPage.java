package com.robindrew.mediamanager.jetty.page;

import static com.robindrew.mediamanager.files.media.MediaType.PHOTO;

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
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.tag.ITag;
import com.robindrew.mediamanager.files.media.tag.ITagCache;
import com.robindrew.mediamanager.files.media.tag.file.IMediaFileTag;
import com.robindrew.mediamanager.files.media.tag.file.IMediaFileTagCache;
import com.robindrew.mediamanager.jetty.page.action.ModifyTagAction;
import com.robindrew.mediamanager.jetty.page.view.MediaFileTagView;

@WebServlet(urlPatterns = "/Photos/Tag")
@TemplateResource("site/media/photos/Tag.html")
public class PhotoTagPage extends AbstractTemplateServlet {

	@Value("${photos.per.page}")
	private int photosPerPage = 6;
	
	@Autowired
	private ITagCache tagCache;
	@Autowired
	private IMediaFileTagCache fileTagCache;
	@Autowired
	private IFileManager fileManager;
	
	@Override
	protected void execute(IHttpRequest request, IHttpResponse response, Map<String, Object> dataMap) {
		super.execute(request, response, dataMap);

		int tagNumber = request.getInteger("tagNumber");
		int pageNumber = request.getInteger("number", 1);
		int pageSize = request.getInteger("size", photosPerPage);
		int tagId = request.getInteger("tagId", -1);
		String tags = request.getString("tag", null);

		new ModifyTagAction().execute(tags, tagId);

		ITag tag = tagCache.getTag(tagNumber);
		Set<IMediaFileTag> fileTags = fileTagCache.getFileTags(tag);
		Set<MediaFileTagView> views = MediaFileTagView.from(fileManager, fileTags, PHOTO);

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
