package com.robindrew.mediamanager.files.archive;

import java.io.File;

import com.robindrew.common.util.Check;

public class ArchivedFile implements IArchivedFile {

	private final File archive;
	private final String name;

	public ArchivedFile(String name, File archive) {
		this.name = Check.notEmpty("name", name);
		this.archive = Check.existsFile("archive", archive);
	}

	public File getArchive() {
		return archive;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPath() {
		return archive.getAbsolutePath() + "#" + getName();
	}

	@Override
	public String toString() {
		return getPath();
	}

}
