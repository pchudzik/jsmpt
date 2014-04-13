package com.pchudzik.jsmtp.common;

/**
 * Created by pawel on 11.04.14.
 */
public class FakeTimeProvider extends TimeProvider {
	private volatile long now;

	public FakeTimeProvider() {
		this(System.currentTimeMillis());
	}

	public FakeTimeProvider(long now) {
		this.now = now;
	}

	@Override
	public long getCurrentTime() {
		return now;
	}

	public FakeTimeProvider setNow(long now) {
		this.now = now;
		return this;
	}
}
