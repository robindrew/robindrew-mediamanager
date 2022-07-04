package com.robindrew.mediamanager.servlet;

import java.io.File;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.io.ByteSource;
import com.robindrew.common.http.MimeType;
import com.robindrew.common.http.response.IHttpResponse;
import com.robindrew.common.http.servlet.AbstractBaseServlet;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.io.file.Files;
import com.robindrew.mediamanager.component.file.cache.IMediaFile;
import com.robindrew.mediamanager.component.file.manager.IMediaFileManager;

@WebServlet(urlPatterns = "/Videos/ViewVideo")
public class ViewVideoServlet extends AbstractBaseServlet {

	@Autowired
	private IMediaFileManager fileManager;
	
	@Override
	public void execute(IHttpRequest request, IHttpResponse response) {

		int id = request.getInteger("id");
		IMediaFile mediaFile = fileManager.getMediaFile(id);

		File file = new File(fileManager.getRootDirectory(), mediaFile.getSourcePath());
		ByteSource video = Files.asByteSource(file);

		String type = MimeType.forExtension(mediaFile.getName());
		response.addHeader("Cache-Control", "public, max-age=604800");
		response.ok(type, video);
	}

}
