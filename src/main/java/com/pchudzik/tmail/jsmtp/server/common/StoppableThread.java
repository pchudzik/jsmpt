package com.pchudzik.tmail.jsmtp.server.common;

/**
 * Created by pawel on 11.04.14.
 */
public class StoppableThread extends Thread {
	private volatile boolean isRunning = true;

	private final RunnableTask task;

	protected StoppableThread(RunnableTask task) {
		this.task = task;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void shutdown() {
		isRunning = false;
	}

	@Override
	public final void run() {
		task.onBeforeRun();
		while(isRunning()) {
			task.run();
		}
		task.onAfterRun();
	}
}
