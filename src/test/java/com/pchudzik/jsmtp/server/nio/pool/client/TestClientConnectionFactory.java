package com.pchudzik.jsmtp.server.nio.pool.client;

import com.pchudzik.jsmtp.common.FakeTimeProvider;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Optional;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

	public ClientConnection newConnectionMock() {
		final ClientContext clientContext = mock(ClientContext.class);
		final ClientConnection connection = mock(ClientConnection.class);
		when(clientContext.getObject(anyString())).thenReturn(Optional.empty());
		when(connection.getClientContext()).thenReturn(clientContext);
		return connection;
	}
}
