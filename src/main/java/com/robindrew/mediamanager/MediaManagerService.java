package com.robindrew.mediamanager;

import com.robindrew.common.service.AbstractService;
import com.robindrew.common.service.component.heartbeat.HeartbeatComponent;
import com.robindrew.common.service.component.logging.LoggingComponent;
import com.robindrew.common.service.component.properties.PropertiesComponent;
import com.robindrew.common.service.component.stats.StatsComponent;
import com.robindrew.mediamanager.files.MediaFileComponent;
import com.robindrew.mediamanager.jetty.JettyComponent;

public class MediaManagerService extends AbstractService {

	/**
	 * Entry point for the TurnEngine Admin Client Service.
	 */
	public static void main(String[] args) {
		MediaManagerService service = new MediaManagerService(args);
		service.startAsync();
	}

	private final JettyComponent jetty = new JettyComponent();
	private final HeartbeatComponent heartbeat = new HeartbeatComponent();
	private final PropertiesComponent properties = new PropertiesComponent();
	private final LoggingComponent logging = new LoggingComponent();
	private final MediaFileComponent file = new MediaFileComponent();
	private final StatsComponent stats = new StatsComponent();

	public MediaManagerService(String[] args) {
		super(args);
	}

	@Override
	protected void startupService() throws Exception {
		start(properties);
		start(logging);
		start(heartbeat);
		start(stats);
		start(file);
		start(jetty);
	}

	@Override
	protected void shutdownService() throws Exception {
		stop(jetty);
		stop(heartbeat);
	}

}
