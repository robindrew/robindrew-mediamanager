package com.robindrew.mediamanager.component.file.loader.frame;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AnimatedGifWriterQueue {

	private final ExecutorService service;
	private final Map<AnimatedGifWriter, FutureTask<byte[]>> futureMap = new LinkedHashMap<>();

	public AnimatedGifWriterQueue(@Value("${gif.writer.threads:5}") int threads) {
		this.service = newFixedThreadPool(threads);
	}

	public Future<byte[]> enqueue(AnimatedGifWriter writer) {
		synchronized (futureMap) {
			FutureTask<byte[]> future = futureMap.get(writer);
			if (future != null) {
				return future;
			}
			future = new FutureTask<>(new ImageCallable(writer));
			futureMap.put(writer, future);
			service.submit(future);
			return future;
		}
	}

	private class ImageCallable implements Callable<byte[]> {

		private final AnimatedGifWriter writer;

		public ImageCallable(AnimatedGifWriter writer) {
			this.writer = writer;
		}

		@Override
		public byte[] call() throws Exception {
			try {
				return writer.writeFrames();
			} finally {
				synchronized (futureMap) {
					futureMap.remove(writer);
				}
			}
		}
	}

}
