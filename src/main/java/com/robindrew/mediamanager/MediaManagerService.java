package com.robindrew.mediamanager;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

import com.robindrew.common.web.Bootstrap;
import com.robindrew.spring.AbstractSpringService;
import com.robindrew.spring.component.indexlink.IndexLinkMap;

@SpringBootApplication
@ComponentScan(basePackages = "com.robindrew.mediamanager")
@ServletComponentScan(basePackages = "com.robindrew.mediamanager")
public class MediaManagerService extends AbstractSpringService {

	public static void main(String[] args) {
		SpringApplication.run(MediaManagerService.class, args);
	}

	@Autowired
	private IndexLinkMap linkMap;

	@PostConstruct
	public void registerLinks() {
		linkMap.add("Photos", "/Photos", Bootstrap.COLOR_SUCCESS);
		linkMap.add("Videos", "/Videos", Bootstrap.COLOR_DANGER);
	}

}
