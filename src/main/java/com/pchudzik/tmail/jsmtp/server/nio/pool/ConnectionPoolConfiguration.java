package com.pchudzik.tmail.jsmtp.server.nio.pool;

/**
 * User: pawel
 * Date: 06.04.14
 * Time: 18:21
 */
public class ConnectionPoolConfiguration {
	private final String threadName;
	private long newClientRegisterTimeout = 250L;
	private int newClientsQueueSize = 1_000;

	public ConnectionPoolConfiguration(String threadName) {
		this.threadName = threadName;
	}

	public long getNewClientRegisterTimeout() {
		return newClientRegisterTimeout;
	}

	public ConnectionPoolConfiguration setNewClientRegisterTimeout(long newClientRegisterTimeout) {
		this.newClientRegisterTimeout = newClientRegisterTimeout;
		return this;
	}

	public int getNewClientsQueueSize() {
		return newClientsQueueSize;
	}

	public ConnectionPoolConfiguration setNewClientsQueueSize(int newClientsQueueSize) {
		this.newClientsQueueSize = newClientsQueueSize;
		return this;
	}

	public String getThreadName() {
		return threadName;
	}
}
