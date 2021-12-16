package com.robindrew.mediamanager.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;

import com.robindrew.common.io.Files;

public class FileRenamerTool {

	public static void main(String[] args) {
		String directory = "D:\\services\\MediaManager\\data\\raw";
		new FileRenamerTool().renameDirectory(new File(directory));
	}

	private void renameDirectory(File directory) {
		System.out.println("[Rename] " + directory);
		int count = 1;

		List<File> files = new ArrayList<>(getContents(directory));
		while (count < 1000) {
			String name = getName(count);
			if (remove(name, files)) {
				count++;
			} else {
				break;
			}
		}
		if (files.isEmpty()) {
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				renameDirectory(file);
			} else {
				renameFile(file, count++);
			}
		}
	}

	private boolean remove(String name, List<File> files) {
		for (File file : files) {
			String fileName = getName(file);
			if (fileName.equals(name)) {
				files.remove(file);
				return true;
			}
		}
		return false;
	}

	private String getName(File file) {
		String name = file.getName();
		int dotIndex = name.indexOf('.');
		if (dotIndex != -1) {
			name = name.substring(0, dotIndex);
		}
		return name;
	}

	private String getExt(File file) {
		String name = file.getName();
		int dotIndex = name.indexOf('.');
		if (dotIndex != -1) {
			name = name.substring(dotIndex + 1);
		}
		return name;
	}

	private String getName(int count) {
		if (count < 10) {
			return "00" + count;
		}
		if (count < 100) {
			return "0" + count;
		}
		return String.valueOf(count);
	}

	private List<File> getContents(File directory) {
		List<File> files = Files.listContents(directory);
		Collections.sort(files, (file1, file2) -> fileComparator(file1, file2));
		return files;
	}

	public int fileComparator(File file1, File file2) {
		CompareToBuilder compare = new CompareToBuilder();
		compare.append(file1.lastModified(), file2.lastModified());
		compare.append(file1.getName(), file2.getName());
		return compare.toComparison();
	}

	private void renameFile(File fromFile, int number) {
		String name = getName(number);
		String ext = getExt(fromFile);
		File toFile = new File(fromFile.getParentFile(), name + "." + ext);
		fromFile.renameTo(toFile);
		System.out.println("[Rename] " + fromFile + " (" + fromFile.lastModified() + ") -> " + toFile);
	}
}
