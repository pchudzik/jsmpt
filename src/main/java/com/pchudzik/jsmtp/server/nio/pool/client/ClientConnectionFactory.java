package com.pchudzik.jsmtp.server.nio.pool.client;

import com.pchudzik.jsmtp.common.TimeProvider;
import com.pchudzik.jsmtp.server.command.rfc821.MailConstans;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.ClientRejectedException;
import com.pchudzik.jsmtp.server.nio.pool.ConnectionsRegistry;

import java.nio.channels.SelectionKey;

/**
 * User: pawel
 * Date: 12.04.14
 * Time: 14:49
 */
public class ClientConnectionFactory {
	final TimeProvider timeProvider;
	final ConnectionsRegistry connectionsRegistry;
	final NewClientProcessor newClientProcessor;

	public ClientConnectionFactory(TimeProvider timeProvider, ConnectionsRegistry connectionsRegistry, NewClientProcessor clientProcessor) {
		this.timeProvider = timeProvider;
		this.connectionsRegistry = connectionsRegistry;
		this.newClientProcessor = clientProcessor;
	}

	public ClientConnection newConnection(SelectionKey selectionKey) throws ClientRejectedException {
		final ClientConnection newConnection = new ClientConnection(
				timeProvider,
				selectionKey,
				new ClientContext().put(MailConstans.mail, new MailTransaction()));
		newClientProcessor.processNewClient(newConnection);
		connectionsRegistry.addNewClient(newConnection);
		return newConnection;
	}

	@FunctionalInterface
	public interface NewClientProcessor {
		void processNewClient(ClientConnection newConnection) throws ClientRejectedException;
	}
}
