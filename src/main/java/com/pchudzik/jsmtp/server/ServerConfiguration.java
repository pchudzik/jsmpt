package com.pchudzik.jsmtp.server;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.experimental.Builder;

/**
 * Created by pawel on 14.04.14.
 */
@Getter @Builder
public class ServerConfiguration {
	private final String listenAddress;
	private final int port;
	private final ConnectionPoolConfiguration connectionPoolConfiguration;

	@Builder @Getter
	public static class ConnectionPoolConfiguration {
		public static final ConnectionPoolConfiguration defaults = ConnectionPoolConfiguration.builder()
				.threadName("connection pool")
				.newClientRegisterTimeout(250L)
				.newClientsQueueSize(1_000)
				.connectionPoolsSize(4)
				.maxKeepAliveTime(TimeUnit.SECONDS.toMillis(60L))
				.checkTickTimeout(TimeUnit.SECONDS.toMillis(1L))
				.build();

		private final String threadName;
		private long newClientRegisterTimeout;
		private int newClientsQueueSize;
		private int connectionPoolsSize;

		private long maxKeepAliveTime;
		private long checkTickTimeout;
	}
}
