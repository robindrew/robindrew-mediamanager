package com.robindrew.mediamanager.jetty.executor;

import static com.robindrew.common.dependency.DependencyFactory.getDependency;

import java.io.File;

import com.google.common.io.ByteSource;
import com.robindrew.common.http.ContentType;
import com.robindrew.common.http.MimeType;
import com.robindrew.common.http.servlet.executor.IHttpExecutor;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.http.servlet.response.IHttpResponse;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;
import com.robindrew.mediamanager.files.media.IMediaFrame;

public class ViewVideoFrameExecutor implements IHttpExecutor {

	@Override
	public void execute(IHttpRequest request, IHttpResponse response) {

		int id = request.getInteger("id");
		int seconds = request.getInteger("s", 5);
		IFileManager manager = getDependency(IFileManager.class);
		IMediaFile mediaFile = manager.getMediaFile(id);

		File file = new File(manager.getRootDirectory(), mediaFile.getSourcePath());

		IMediaFrame frame = mediaFile.getFrame(file, seconds);

		response.addHeader("Cache-Control", "public, max-age=604800");
		response.ok(new ContentType(MimeType.IMAGE_JPEG), ByteSource.wrap(frame.toByteArray()));
	}

}
