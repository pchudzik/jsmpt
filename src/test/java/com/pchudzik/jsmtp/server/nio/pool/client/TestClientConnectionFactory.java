package com.pchudzik.jsmtp.server.nio.pool.client;

import com.pchudzik.jsmtp.common.FakeTimeProvider;
import com.pchudzik.jsmtp.server.nio.pool.ConnectionsRegistry;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: pawel
 * Date: 13.04.14
 * Time: 10:09
 */
public class TestClientConnectionFactory extends ClientConnectionFactory {
	public TestClientConnectionFactory() {
		this(newConnection -> {});
	}

	public TestClientConnectionFactory(NewClientProcessor processor) {
		super(new FakeTimeProvider(), mock(ConnectionsRegistry.class), processor);
	}

	public FakeTimeProvider getTimeProvider() {
		return (FakeTimeProvider) timeProvider;
	}

	public ConnectionsRegistry getConnectionRegistry() {
		return connectionsRegistry;
	}

	public SocketChannel mockSocketChannel() {
		return mock(SocketChannel.class);
	}

	public SelectionKey mockSelectionKeyWithChannel() {
		return mockSelectionKeyWithChannel(mockSocketChannel());
	}

	public SelectionKey mockSelectionKeyWithChannel(SocketChannel socketChannel) {
		final SelectionKey selectionKey = mock(SelectionKey.class);
		when(selectionKey.channel()).thenReturn(socketChannel);
		return selectionKey;
	}
}
