package com.pchudzik.jsmtp.server.nio.pool;

import com.pchudzik.jsmtp.common.FakeTimeProvider;

import static org.mockito.Mockito.mock;

/**
 * User: pawel
 * Date: 13.04.14
 * Time: 10:09
 */
public class TestClientConnectionFactory extends ClientConnectionFactory {
	public TestClientConnectionFactory() {
		super(new FakeTimeProvider(), mock(ConnectionsRegistry.class));
	}

	public FakeTimeProvider getTimeProvider() {
		return (FakeTimeProvider) timeProvider;
	}

	public ConnectionsRegistry getConnectionRegistry() {
		return connectionsRegistry;
	}
}
