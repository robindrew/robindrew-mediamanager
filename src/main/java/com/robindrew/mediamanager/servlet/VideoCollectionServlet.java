package com.robindrew.mediamanager.servlet;

import static com.robindrew.mediamanager.component.file.cache.MediaType.VIDEO;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Splitter;
import com.robindrew.common.collect.IPaginator;
import com.robindrew.common.collect.Paginator;
import com.robindrew.common.http.response.IHttpResponse;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.mediamanager.component.file.cache.IMediaFile;
import com.robindrew.mediamanager.component.file.cache.MediaFileCollection;
import com.robindrew.mediamanager.component.file.manager.IMediaFileManager;
import com.robindrew.mediamanager.component.file.tagcache.IMediaFileTagCache;
import com.robindrew.mediamanager.servlet.action.ModifyTagAction;
import com.robindrew.spring.component.servlet.template.AbstractTemplateServlet;
import com.robindrew.spring.component.servlet.template.TemplateResource;

@WebServlet(urlPatterns = "/Videos/Collection")
@TemplateResource("templates/videos/Collection.html")
public class VideoCollectionServlet extends AbstractTemplateServlet {

	@Value("${videos.per.page:16}")
	private int defaultVideosPerPage;

	@Autowired
	private IMediaFileManager fileManager;
	@Autowired
	private IMediaFileTagCache fileTagCache;

	@Override
	protected void execute(IHttpRequest request, IHttpResponse response, Map<String, Object> dataMap) {
		super.execute(request, response, dataMap);

		int pageNumber = request.getInteger("number", 1);
		int pageSize = request.getInteger("size", defaultVideosPerPage);
		String tags = request.getString("tag", null);
		int tagId = request.getInteger("tagId", -1);
		String allTags = request.getString("allTags", null);

		new ModifyTagAction(fileTagCache).execute(tags, tagId);

		Set<IMediaFile> files = fileManager.getMediaFiles();
		List<IMediaFile> filtered = MediaFileCollection.filterByType(VIDEO, files);

		IPaginator<IMediaFile> paginator = new Paginator<>(filtered);
		filtered = paginator.getPage(pageNumber, pageSize);
		int pageCount = paginator.getPageCount(pageSize);
		
		// Tag All command
		if (allTags != null) {
			for (String tagName : Splitter.on(',').omitEmptyStrings().trimResults().split(allTags)) {
				for (IMediaFile file : files) {
					fileTagCache.add(file.getId(), tagName);
				}
			}
		}


		dataMap.put("root", fileManager.getRootDirectory());
		dataMap.put("files", filtered);
		dataMap.put("pageSize", pageSize);
		dataMap.put("previousPage", pageNumber - 1);
		dataMap.put("currentPage", pageNumber);
		dataMap.put("nextPage", pageCount == pageNumber ? 0 : pageNumber + 1);
		dataMap.put("pageCount", pageCount);
	}

}
