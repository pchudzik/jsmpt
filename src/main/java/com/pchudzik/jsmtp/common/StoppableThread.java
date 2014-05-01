package com.pchudzik.jsmtp.common;

/**
 * Created by pawel on 11.04.14.
 */
public class StoppableThread extends Thread {
	private volatile boolean isRunning = true;

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
		while(isRunning()) {
			task.run();
		}
		task.onAfterRun();
	}
}
