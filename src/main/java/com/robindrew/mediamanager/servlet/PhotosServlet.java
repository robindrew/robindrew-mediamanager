package com.robindrew.mediamanager.servlet;

import static com.robindrew.mediamanager.component.file.cache.MediaType.PHOTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import com.robindrew.common.http.response.IHttpResponse;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.http.servlet.template.AbstractTemplateServlet;
import com.robindrew.common.http.servlet.template.TemplateResource;
import com.robindrew.mediamanager.component.file.cache.IMediaFile;
import com.robindrew.mediamanager.component.file.cache.IMediaFileCollection;
import com.robindrew.mediamanager.component.file.cache.MediaFileCollection;
import com.robindrew.mediamanager.component.file.manager.IMediaFileManager;
import com.robindrew.mediamanager.component.file.tagcache.IMediaFileTagCache;

@WebServlet(urlPatterns = "/Photos")
@TemplateResource("site/media/Photos.html")
public class PhotosServlet extends AbstractTemplateServlet {

	@Autowired
	private IMediaFileTagCache tagCache;
	@Autowired
	private IMediaFileManager fileManager;
	
	@Override
	protected void execute(IHttpRequest request, IHttpResponse response, Map<String, Object> dataMap) {
		super.execute(request, response, dataMap);

		Set<IMediaFile> files = fileManager.getMediaFiles();
		List<IMediaFileCollection> collections = MediaFileCollection.splitToListWithType(PHOTO, files);

		dataMap.put("root", fileManager.getRootDirectory());
		dataMap.put("collections", collections);
		dataMap.put("tagCache", tagCache);
	}

}
