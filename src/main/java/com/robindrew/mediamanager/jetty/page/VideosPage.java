package com.robindrew.mediamanager.jetty.page;

import static com.robindrew.mediamanager.files.media.MediaType.VIDEO;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import com.robindrew.common.http.response.IHttpResponse;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.http.servlet.template.AbstractTemplateServlet;
import com.robindrew.common.http.servlet.template.TemplateResource;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;
import com.robindrew.mediamanager.files.media.IMediaFileCollection;
import com.robindrew.mediamanager.files.media.MediaFileCollection;
import com.robindrew.mediamanager.files.media.tag.file.IMediaFileTagCache;

@WebServlet(urlPatterns = "/Videos")
@TemplateResource("site/media/Videos.html")
public class VideosPage extends AbstractTemplateServlet {

	@Autowired
	private IFileManager fileManager;
	@Autowired
	private IMediaFileTagCache fileTagCache;

	@Override
	protected void execute(IHttpRequest request, IHttpResponse response, Map<String, Object> dataMap) {
		super.execute(request, response, dataMap);

		Set<IMediaFile> files = fileManager.getMediaFiles();
		List<IMediaFileCollection> collections = MediaFileCollection.splitToListWithType(VIDEO, files);

		dataMap.put("root", fileManager.getRootDirectory());
		dataMap.put("collections", collections);
		dataMap.put("tagCache", fileTagCache);
	}

}
