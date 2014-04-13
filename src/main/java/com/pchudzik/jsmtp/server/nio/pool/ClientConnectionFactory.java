package com.pchudzik.jsmtp.server.nio.pool;

import com.pchudzik.jsmtp.common.TimeProvider;

import java.nio.channels.SelectionKey;

/**
 * User: pawel
 * Date: 12.04.14
 * Time: 14:49
 */
class ClientConnectionFactory {
	final TimeProvider timeProvider;
	final ConnectionsRegistry connectionsRegistry;

	public ClientConnectionFactory(TimeProvider timeProvider, ConnectionsRegistry connectionsRegistry) {
		this.timeProvider = timeProvider;
		this.connectionsRegistry = connectionsRegistry;
	}

	public ClientConnection newConnection(SelectionKey selectionKey) throws ClientRejectedException {
		final ClientConnection newConnection = new ClientConnection(timeProvider, selectionKey);
		connectionsRegistry.addNewClient(newConnection);
		return newConnection;
	}
}
