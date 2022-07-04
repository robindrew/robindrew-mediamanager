package com.robindrew.mediamanager.jetty.executor;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.io.ByteSource;
import com.robindrew.common.http.MimeType;
import com.robindrew.common.http.response.IHttpResponse;
import com.robindrew.common.http.servlet.executor.IHttpExecutor;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.io.file.Files;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;

public class ViewVideoExecutor implements IHttpExecutor {

	@Autowired
	private IFileManager fileManager;
	
	@Override
	public void execute(IHttpRequest request, IHttpResponse response) {

		int id = request.getInteger("id");
		IMediaFile mediaFile = fileManager.getMediaFile(id);

		File file = new File(manager.getRootDirectory(), mediaFile.getSourcePath());
		ByteSource video = Files.asByteSource(file);

		String type = MimeType.forExtension(mediaFile.getName());
		response.addHeader("Cache-Control", "public, max-age=604800");
		response.ok(type, video);
	}

}
