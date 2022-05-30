package com.robindrew.mediamanager.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileConverterTool {

	public static void main(String[] args) throws Exception {
		ExecutorService service = Executors.newCachedThreadPool();

		List<String> command = Arrays.asList("D:/Temp/ffmpeg-5.0/bin/convertWmv.bat");
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File("D:\\Temp\\ffmpeg-5.0\\bin"));
		Process process = builder.start();
		service.submit(() -> print(process.getInputStream()));
		service.submit(() -> print(process.getErrorStream()));
		System.out.println(process.waitFor());
		service.shutdown();
	}

	private static void print(InputStream input) {
		input = new BufferedInputStream(input);
		try {
			while (true) {
				int read = input.read();
				if (read == -1) {
					System.out.println();
					break;
				}
				System.out.print((char) read);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
