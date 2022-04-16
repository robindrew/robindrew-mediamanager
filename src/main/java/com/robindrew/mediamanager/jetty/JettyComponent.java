package com.robindrew.mediamanager.jetty;

import com.google.common.base.Supplier;
import com.robindrew.common.html.Bootstrap;
import com.robindrew.common.http.servlet.executor.IHttpExecutor;
import com.robindrew.common.http.servlet.executor.IVelocityHttpContext;
import com.robindrew.common.service.component.jetty.JettyVelocityComponent;
import com.robindrew.common.service.component.jetty.handler.MatcherHttpHandler;
import com.robindrew.common.service.component.jetty.handler.page.BeanConsolePage;
import com.robindrew.common.service.component.jetty.handler.page.BeanOperationPage;
import com.robindrew.common.service.component.jetty.handler.page.BeanViewPage;
import com.robindrew.common.service.component.jetty.handler.page.GetBeanAttributePage;
import com.robindrew.common.service.component.jetty.handler.page.IndexPage;
import com.robindrew.common.service.component.jetty.handler.page.SetBeanAttributePage;
import com.robindrew.common.service.component.jetty.handler.page.SystemPage;
import com.robindrew.common.template.ITemplateLocator;
import com.robindrew.common.template.velocity.VelocityTemplateLocatorSupplier;
import com.robindrew.mediamanager.jetty.executor.ViewPhotoExecutor;
import com.robindrew.mediamanager.jetty.executor.ViewVideoExecutor;
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

		// Register standard pages
		handler.uri("/", newIndexPage(getContext(), "site/common/Index.html"));
		handler.uri("/System", new SystemPage(getContext(), "site/common/System.html"));
		handler.uri("/BeanConsole", new BeanConsolePage(getContext(), "site/common/BeanConsole.html"));
		handler.uri("/BeanView", new BeanViewPage(getContext(), "site/common/BeanView.html"));
		handler.uri("/BeanOperation", new BeanOperationPage(getContext(), "site/common/BeanOperation.html"));
		handler.uri("/GetBeanAttribute", new GetBeanAttributePage(getContext(), "site/common/GetBeanAttribute.html"));
		handler.uri("/SetBeanAttribute", new SetBeanAttributePage(getContext(), "site/common/SetBeanAttribute.html"));

		// Media manager pages
		handler.uri("/Photos", new PhotosPage(getContext(), "site/media/Photos.html"));
		handler.uri("/Photos/Tag", new PhotoTagPage(getContext(), "site/media/photos/Tag.html"));
		handler.uri("/Photos/Collection", new PhotoCollectionPage(getContext(), "site/media/photos/Collection.html"));
		handler.uriPattern("/Photos/ViewPhoto/.*", new ViewPhotoExecutor());

		handler.uri("/Videos", new VideosPage(getContext(), "site/media/Videos.html"));
		handler.uri("/Videos/Tag", new VideoTagPage(getContext(), "site/media/videos/Tag.html"));
		handler.uri("/Videos/Collection", new VideoCollectionPage(getContext(), "site/media/videos/Collection.html"));
		handler.uri("/Videos/ViewVideo", new ViewVideoExecutor());
	}

	private IHttpExecutor newIndexPage(IVelocityHttpContext context, String templateName) {
		IndexPage page = new IndexPage(context, templateName);
		page.addLink("Photos", "/Photos", Bootstrap.COLOR_SUCCESS);
		page.addLink("Videos", "/Videos", Bootstrap.COLOR_DANGER);
		return page;
	}

}
