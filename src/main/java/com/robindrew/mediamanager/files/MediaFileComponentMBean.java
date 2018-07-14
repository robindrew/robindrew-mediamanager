package com.robindrew.mediamanager.files;

import java.util.Set;

public interface MediaFileComponentMBean {

	String getRootDirectory();

	int getMediaFileCount();

	Set<String> getMediaFiles();

}
