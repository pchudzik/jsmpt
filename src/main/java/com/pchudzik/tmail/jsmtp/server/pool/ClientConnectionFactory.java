package com.pchudzik.tmail.jsmtp.server.pool;

import com.pchudzik.tmail.jsmtp.server.ClientRejectedException;

import java.nio.channels.SelectionKey;

/**
 * User: pawel
 * Date: 12.04.14
 * Time: 14:49
 */
class ClientConnectionFactory {
	final ConnectionsRegistry connectionsRegistry;

	public ClientConnectionFactory(ConnectionsRegistry connectionsRegistry) {
		this.connectionsRegistry = connectionsRegistry;
	}

	public ClientConnection newConnection(SelectionKey selectionKey) throws ClientRejectedException {
		final ClientConnection newConnection = new ClientConnection(selectionKey);
		connectionsRegistry.addNewClient(newConnection);
		return newConnection;
	}
}
