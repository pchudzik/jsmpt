package com.pchudzik.jsmtp.common;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pawel on 11.04.14.
 */
public class StoppableThreadTest {
	@Test
	public void shouldStopThreadWhenRequested() throws Exception {
		StoppableThread thread = new StoppableThread(() -> {
			//no operation just loop forever
		});

		start(thread);
		assertThat(thread.isRunning()).isTrue();
		assertThat(thread.isAlive()).isTrue();

		stop(thread);
		assertThat(thread.isRunning()).isFalse();
		assertThat(thread.isAlive()).isFalse();
	}

	private void start(StoppableThread thread) throws Exception {
		thread.start();
		Thread.sleep(50L);
	}

	private void stop(StoppableThread thread) throws InterruptedException {
		thread.shutdown();
		Thread.sleep(50L);
	}
}
