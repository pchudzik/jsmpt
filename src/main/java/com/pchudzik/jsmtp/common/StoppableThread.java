package com.pchudzik.jsmtp.common;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by pawel on 11.04.14.
 */
public class StoppableThread extends Thread implements Closeable {
	private volatile boolean isRunning = false;

	private final RunnableTask task;

	public StoppableThread(RunnableTask task) {
		this.task = task;
	}

	public StoppableThread(RunnableTask task, String threadName) {
		this.task = task;
		setName(threadName);
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void shutdown() {
		isRunning = false;
		task.onClose();
	}

	@Override
	public final void run() {
		task.onBeforeRun();
		isRunning = true;

		while(isRunning()) {
			task.run();
		}

		task.onAfterRun();
	}

	@Override
	public void close() throws IOException {
		shutdown();
	}
}
