package com.robindrew.mediamanager.files.archive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.ByteSource;
import com.robindrew.common.base.Java;
import com.robindrew.common.io.file.Files;

public class ZipFileArchive extends FileArchive {

	public ZipFileArchive(File file) {
		super(file);
	}

	@Override
	public List<IArchivedFile> getArchivedFiles() {
		ByteSource source = Files.asByteSource(getFile());
		try (ZipInputStream input = new ZipInputStream(source.openBufferedStream())) {

			List<IArchivedFile> list = new ArrayList<>();
			while (true) {
				ZipEntry entry = input.getNextEntry();
				if (entry == null) {
					break;
				}
				if (!entry.isDirectory()) {
					String name = entry.getName();
					list.add(new ArchivedFile(name, getFile()));
				}
			}

			return list;

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

}
