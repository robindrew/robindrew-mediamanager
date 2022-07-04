package com.robindrew.mediamanager.jetty.page;

import static com.robindrew.mediamanager.files.media.MediaType.VIDEO;

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
import com.robindrew.common.http.servlet.template.AbstractTemplateServlet;
import com.robindrew.common.http.servlet.template.TemplateResource;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;
import com.robindrew.mediamanager.files.media.IMediaFileCollection;
import com.robindrew.mediamanager.files.media.MediaFileCollection;
import com.robindrew.mediamanager.files.media.tag.file.IMediaFileTagCache;
import com.robindrew.mediamanager.jetty.page.action.ModifyTagAction;

@WebServlet(urlPatterns = "/Videos/Collection")
@TemplateResource("site/media/videos/Collection.html")
public class VideoCollectionPage extends AbstractTemplateServlet {

	@Value("${videos.per.page}")
	private int defaultVideosPerPage = 10;

	@Autowired
	private IFileManager fileManager;
	@Autowired
	private IMediaFileTagCache fileTagCache;

	@Override
	protected void execute(IHttpRequest request, IHttpResponse response, Map<String, Object> dataMap) {
		super.execute(request, response, dataMap);

		String name = request.getString("name");
		String type = request.getString("type", "name");
		int pageNumber = request.getInteger("number", 1);
		int pageSize = request.getInteger("size", defaultVideosPerPage);
		String tags = request.getString("tag", null);
		int tagId = request.getInteger("tagId", -1);
		String allTags = request.getString("allTags", null);

		new ModifyTagAction(fileTagCache).execute(tags, tagId);

		Set<IMediaFile> files = fileManager.getMediaFiles();
		List<IMediaFileCollection> collections = MediaFileCollection.splitToListWithType(VIDEO, files);

		int index = indexOf(collections, name, type);
		IMediaFileCollection collection = collections.get(index);
		files = collection.getFiles();

		String nextName = (index == (collections.size() - 1)) ? name : collections.get(index + 1).getName();
		String prevName = (index == 0) ? name : collections.get(index - 1).getName();

		// Tag All command
		if (allTags != null) {
			for (String tagName : Splitter.on(',').omitEmptyStrings().trimResults().split(allTags)) {
				for (IMediaFile file : files) {
					fileTagCache.add(file.getId(), tagName);
				}
			}
		}

		IPaginator<IMediaFile> paginator = new Paginator<>(files);
		List<IMediaFile> page = paginator.getPage(pageNumber, pageSize);
		List<IMediaFile> next = paginator.getPage(pageNumber + 1, pageSize);
		int pageCount = paginator.getPageCount(pageSize);

		dataMap.put("root", fileManager.getRootDirectory());
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
