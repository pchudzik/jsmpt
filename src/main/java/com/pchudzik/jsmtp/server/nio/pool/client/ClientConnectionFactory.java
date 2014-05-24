package com.pchudzik.jsmtp.server.nio.pool.client;

import java.nio.channels.SelectionKey;

import com.pchudzik.jsmtp.common.TimeProvider;
import com.pchudzik.jsmtp.server.nio.pool.ClientRejectedException;
import lombok.RequiredArgsConstructor;

/**
 * User: pawel
 * Date: 12.04.14
 * Time: 14:49
 */
@RequiredArgsConstructor
public class ClientConnectionFactory {
	final TimeProvider timeProvider;
	final ConnectionsRegistry connectionsRegistry;

	public ClientConnection newConnection(SelectionKey selectionKey) throws ClientRejectedException {
		final ClientConnection newConnection = new ClientConnection(
				timeProvider,
				selectionKey,
				new ClientContext());
		connectionsRegistry.addNewClient(newConnection);
		return newConnection;
	}
}
