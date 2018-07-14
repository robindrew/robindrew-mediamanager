package com.robindrew.mediamanager.jetty.executor;

import static com.robindrew.common.dependency.DependencyFactory.getDependency;

import com.google.common.io.ByteSource;
import com.robindrew.common.http.ContentType;
import com.robindrew.common.http.MimeType;
import com.robindrew.common.http.servlet.executor.IHttpExecutor;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.http.servlet.response.IHttpResponse;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;
import com.robindrew.mediamanager.files.media.loader.IMediaFileLoader;

public class ViewPhotoExecutor implements IHttpExecutor {

	@Override
	public void execute(IHttpRequest request, IHttpResponse response) {
		int id = request.getInteger("id");
		int width = request.getInteger("width", 0);
		int height = request.getInteger("height", 0);
		boolean fit = request.getBoolean("fit", true);

		IFileManager manager = getDependency(IFileManager.class);
		IMediaFile mediaFile = manager.getMediaFile(id);

		IMediaFileLoader loader = getDependency(IMediaFileLoader.class);
		byte[] image = loader.getImage(mediaFile, width, height, fit);

		MimeType type = MimeType.withName(mediaFile.getName());
		response.ok(new ContentType(type), ByteSource.wrap(image));
	}

}
