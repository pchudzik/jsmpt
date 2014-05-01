package com.pchudzik.jsmtp.common;

/**
 * Created by pawel on 11.04.14.
 */
@FunctionalInterface
public interface RunnableTask extends Runnable {
	default void onClose() {}
	default void onBeforeRun() {}
	default void onAfterRun() {}
}
