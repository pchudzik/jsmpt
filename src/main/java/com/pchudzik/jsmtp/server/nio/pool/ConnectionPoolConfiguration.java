package com.pchudzik.jsmtp.server.nio.pool;

import lombok.Getter;

/**
 * User: pawel
 * Date: 06.04.14
 * Time: 18:21
 */
@Getter
public class ConnectionPoolConfiguration {
	private final String threadName;
	private long newClientRegisterTimeout = 250L;
	private int newClientsQueueSize = 1_000;
	private int connectionPoolsSize;

	public ConnectionPoolConfiguration(String threadName) {
		this.threadName = threadName;
	}

	public ConnectionPoolConfiguration setNewClientRegisterTimeout(long newClientRegisterTimeout) {
		this.newClientRegisterTimeout = newClientRegisterTimeout;
		return this;
	}

	public ConnectionPoolConfiguration setNewClientsQueueSize(int newClientsQueueSize) {
		this.newClientsQueueSize = newClientsQueueSize;
		return this;
	}

	public ConnectionPoolConfiguration setConnectionPoolsSize(int connectionPoolsSize) {
		this.connectionPoolsSize = connectionPoolsSize;
		return this;
	}
}
