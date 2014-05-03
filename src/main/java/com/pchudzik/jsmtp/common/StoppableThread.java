package com.pchudzik.jsmtp.common;

import java.io.Closeable;
import java.io.IOException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by pawel on 11.04.14.
 */
@Slf4j
public class StoppableThread extends Thread implements Closeable {
	@Getter private volatile boolean isRunning = false;
	@Getter private volatile boolean isFinished = false;

	private final RunnableTask task;

	private static RunnableTask asRunnableTask(Runnable runnable) {
		return () -> runnable.run();
	}

	public StoppableThread(Runnable runnable) {
		this(asRunnableTask(runnable));
	}

	public StoppableThread(Runnable runnable, String threadName) {
		this(asRunnableTask(runnable), threadName);
	}

	public StoppableThread(RunnableTask task) {
		this.task = task;
	}

	public StoppableThread(RunnableTask task, String threadName) {
		this.task = task;
		setName(threadName);
	}

	public void shutdown() {
		isRunning = false;
		task.onClose();
	}

	@Override
	public final void run() {
		log.info("Starting thread {}", getName());
		task.onBeforeRun();
		isRunning = true;

		while(isRunning()) {
			task.run();
		}

		task.onAfterRun();
		isFinished = true;
		log.info("Thread {} finished", getName());
	}

	@Override
	public void close() throws IOException {
		shutdown();
	}
}
