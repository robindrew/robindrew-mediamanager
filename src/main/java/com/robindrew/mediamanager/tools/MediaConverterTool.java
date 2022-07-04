package com.robindrew.mediamanager.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robindrew.common.io.file.Files;

public class MediaConverterTool implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(MediaConverterTool.class);

	private static final String EXE_FILENAME = "ffmpeg.exe";

	public static void main(String[] args) throws Exception {
		String inputDir = "d:/temp/convert/input";
		String outputDir = "d:/temp/convert/output";

		File path = new File("exe");

		for (File inputFile : Files.listFiles(new File(inputDir), true)) {

			// Already an MP4
			String inputName = inputFile.getName();
			if (inputName.endsWith(".mp4")) {
				File renameFile = new File(outputDir, inputName);
				inputFile.renameTo(renameFile);
				continue;
			}

			File outputFile = new File(outputDir, inputName + ".mp4");
			try (MediaConverterTool converter = new MediaConverterTool(path)) {
				converter.convert(inputFile, outputFile);
			}
		}

	}

	private final File path;
	private final ExecutorService service = Executors.newCachedThreadPool();

	public MediaConverterTool(File path) {
		if (!path.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: '" + path + "'");
		}
		if (!path.exists()) {
			throw new IllegalArgumentException("Directory does not exist: '" + path + "'");
		}
		this.path = path;
	}

	public void convert(File inputFile, File outputFile) throws Exception {
		if (outputFile.exists()) {
			return;
		}

		log.info("[Input File] {}", inputFile.getAbsolutePath());
		log.info("[Output File] {}", outputFile.getAbsolutePath());

		// Checks
		File exeFile = new File(path, EXE_FILENAME);
		if (!exeFile.exists()) {
			throw new IllegalStateException("Exe not found: '" + exeFile + "'");
		}
		if (!inputFile.exists()) {
			throw new IllegalStateException("Input file not found: '" + inputFile + "'");
		}

		log.info("[Exe File] {}", exeFile);

		List<String> command = Arrays.asList(exeFile.getAbsolutePath(), "-i", inputFile.getAbsolutePath(), "-c:v", "libx264", "-crf", "18", "-profile:v", "high", "-r", "30", "-c:a", "aac", "-q:a", "100", "-ar", "48000", outputFile.getAbsolutePath());
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File("exe/").getAbsoluteFile());
		Process process = builder.start();
		service.submit(() -> print("Input", process.getInputStream()));
		service.submit(() -> print("Error", process.getErrorStream()));
		log.info("[Complete] {}", process.waitFor());
		service.shutdown();
	}

	private static void print(String name, InputStream input) {
		StringBuilder line = new StringBuilder();
		input = new BufferedInputStream(input);
		try {
			while (true) {
				int read = input.read();
				if (read == -1) {
					break;
				}
				if (read == '\r' || read == '\n') {
					if (line.length() > 0) {
						log.info("[{}] {}", name, line);
					}
					line.setLength(0);
					continue;
				}
				line.append((char) read);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		if (line.length() > 0) {
			log.info("[{}] {}", name, line);
		}
	}

	@Override
	public void close() throws Exception {
		service.shutdown();
	}

}
