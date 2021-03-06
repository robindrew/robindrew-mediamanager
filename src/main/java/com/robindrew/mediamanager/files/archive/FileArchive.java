package com.robindrew.mediamanager.files.archive;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.io.File;
import java.util.Optional;

import com.robindrew.common.util.Check;

public abstract class FileArchive implements IFileArchive {

	public static Optional<IFileArchive> getFileArchive(File file) {
		String filename = file.getName().toLowerCase();
		if (filename.endsWith(".zip")) {
			return of(new ZipFileArchive(file));
		}
		return empty();
	}

	private final File file;

	protected FileArchive(File file) {
		this.file = Check.existsFile("file", file);
	}

	public File getFile() {
		return file;
	}

	@Override
	public String toString() {
		return file.getAbsolutePath();
	}

}
