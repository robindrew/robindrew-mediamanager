package com.robindrew.mediamanager.jetty.executor;

import static com.robindrew.common.dependency.DependencyFactory.getDependency;

import java.io.File;

import com.google.common.io.ByteSource;
import com.robindrew.common.http.ContentType;
import com.robindrew.common.http.MimeType;
import com.robindrew.common.http.servlet.executor.IHttpExecutor;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.http.servlet.response.IHttpResponse;
import com.robindrew.common.io.Files;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;

public class ViewVideoExecutor implements IHttpExecutor {

	@Override
	public void execute(IHttpRequest request, IHttpResponse response) {
		int id = request.getInteger("id");

		IFileManager manager = getDependency(IFileManager.class);
		IMediaFile mediaFile = manager.getMediaFile(id);

		File file = new File(manager.getRootDirectory(), mediaFile.getSourcePath());
		ByteSource video = Files.asByteSource(file);

		MimeType type = MimeType.withName(mediaFile.getName());
		response.ok(new ContentType(type), video);
	}

}
