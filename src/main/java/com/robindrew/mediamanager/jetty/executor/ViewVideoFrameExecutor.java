package com.robindrew.mediamanager.jetty.executor;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.io.ByteSource;
import com.robindrew.common.http.MimeType;
import com.robindrew.common.http.response.IHttpResponse;
import com.robindrew.common.http.servlet.AbstractBaseServlet;
import com.robindrew.common.http.servlet.request.IHttpRequest;
import com.robindrew.mediamanager.files.manager.IFileManager;
import com.robindrew.mediamanager.files.media.IMediaFile;
import com.robindrew.mediamanager.files.media.loader.IMediaFileLoader;
import com.robindrew.mediamanager.files.media.loader.LoaderContext;

@WebServlet(urlPatterns = "/Videos/ViewVideoFrame")
public class ViewVideoFrameExecutor extends AbstractBaseServlet {

	@Autowired
	private IFileManager fileManager;
	@Autowired
	private IMediaFileLoader loader;

	@Override
	public void execute(IHttpRequest request, IHttpResponse response) {

		int id = request.getInteger("id");
		int width = request.getInteger("width", 0);
		int height = request.getInteger("height", 0);
		int seconds = request.getInteger("s", 5);
		boolean fit = request.getBoolean("fit", true);

		IMediaFile mediaFile = fileManager.getMediaFile(id);

		byte[] image = loader.getImage(new LoaderContext(mediaFile, width, height).setFit(fit).setFrameSeconds(seconds));

		response.addHeader("Cache-Control", "public, max-age=604800");
		response.ok(MimeType.IMAGE_JPEG, ByteSource.wrap(image));
	}

}
