package com.robindrew.mediamanager.jetty.executor;

import static com.robindrew.common.dependency.DependencyFactory.getDependency;

import com.google.common.io.ByteSource;
import com.robindrew.common.dependency.DependencyFactory;
import com.robindrew.common.http.ContentType;
import com.robindrew.common.http.MimeType;
import com.robindrew.common.http.servlet.executor.IHttpExecutor;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.common.http.servlet.response.IHttpResponse;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;
import com.robindrew.mediamanager.files.media.loader.IMediaFileLoader;
import com.robindrew.mediamanager.files.media.loader.LoaderContext;

public class ViewVideoFrameExecutor implements IHttpExecutor {

	@Override
	public void execute(IHttpRequest request, IHttpResponse response) {

		int id = request.getInteger("id");
		int width = request.getInteger("width", 0);
		int height = request.getInteger("height", 0);
		int seconds = request.getInteger("s", 5);
		boolean fit = request.getBoolean("fit", true);

		IFileManager manager = getDependency(IFileManager.class);
		IMediaFile mediaFile = manager.getMediaFile(id);

		IMediaFileLoader loader = DependencyFactory.getDependency(IMediaFileLoader.class);
		byte[] image = loader.getImage(new LoaderContext(mediaFile, width, height).setFit(fit).setFrameSeconds(seconds));

		response.addHeader("Cache-Control", "public, max-age=604800");
		response.ok(new ContentType(MimeType.IMAGE_JPEG), ByteSource.wrap(image));
	}

}
