package com.robindrew.mediamanager.servlet;

import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import com.robindrew.common.http.response.IHttpResponse;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.mediamanager.component.file.cache.IMediaFile;
import com.robindrew.mediamanager.component.file.loader.IMediaFileLoader;
import com.robindrew.mediamanager.component.file.manager.IMediaFileManager;
import com.robindrew.mediamanager.component.file.tagcache.IMediaFileTagCache;
import com.robindrew.mediamanager.component.tag.ITagCache;
import com.robindrew.spring.component.servlet.template.AbstractTemplateServlet;
import com.robindrew.spring.component.servlet.template.TemplateResource;

@WebServlet(urlPatterns = "/Videos/Viewer")
@TemplateResource("templates/videos/Viewer.html")
public class VideoViewerServlet extends AbstractTemplateServlet {

	@Autowired
	private ITagCache tagCache;
	@Autowired
	private IMediaFileTagCache fileTagCache;
	@Autowired
	private IMediaFileManager fileManager;
	@Autowired
	private IMediaFileLoader loader;

	@Override
	protected void execute(IHttpRequest request, IHttpResponse response, Map<String, Object> dataMap) {
		super.execute(request, response, dataMap);

		int id = request.getInteger("id");

		IMediaFile mediaFile = fileManager.getMediaFile(id);

		dataMap.put("mediaFile", mediaFile);
	}
}
