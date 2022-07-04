package com.robindrew.mediamanager.jetty;

import com.google.common.base.Supplier;
import com.robindrew.common.http.servlet.executor.IHttpExecutor;
import com.robindrew.common.http.servlet.executor.IVelocityHttpContext;
import com.robindrew.common.service.component.jetty.JettyVelocityComponent;
import com.robindrew.common.service.component.jetty.handler.MatcherHttpHandler;
import com.robindrew.common.service.component.jetty.handler.page.IndexPage;
import com.robindrew.common.template.ITemplateLocator;
import com.robindrew.common.template.velocity.VelocityTemplateLocatorSupplier;
import com.robindrew.common.web.Bootstrap;
import com.robindrew.mediamanager.jetty.executor.ViewPhotoExecutor;
import com.robindrew.mediamanager.jetty.executor.ViewVideoExecutor;
import com.robindrew.mediamanager.jetty.executor.ViewVideoFrameExecutor;
import com.robindrew.mediamanager.jetty.page.PhotoCollectionPage;
import com.robindrew.mediamanager.jetty.page.PhotoTagPage;
import com.robindrew.mediamanager.jetty.page.PhotosPage;
import com.robindrew.mediamanager.jetty.page.VideoCollectionPage;
import com.robindrew.mediamanager.jetty.page.VideoTagPage;
import com.robindrew.mediamanager.jetty.page.VideosPage;

public class JettyComponent extends JettyVelocityComponent {

	@Override
	protected Supplier<ITemplateLocator> getTemplateLocator() {
		return new VelocityTemplateLocatorSupplier();
	}

	@Override
	protected void populate(MatcherHttpHandler handler) {
		handler.uriPattern("/Photos/ViewPhoto/.*", new ViewPhotoExecutor());
		handler.uri("/Videos/ViewVideo", new ViewVideoExecutor());
		handler.uri("/Videos/ViewVideoFrame", new ViewVideoFrameExecutor());
	}


}
