package com.pchudzik.jsmtp.server.nio.pool;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.pchudzik.jsmtp.common.RunnableTask;
import com.pchudzik.jsmtp.common.TimeProvider;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * User: pawel
 * Date: 10.04.14
 * Time: 18:47
 */
public class ConnectionsRegistry implements RunnableTask {
	private static final Logger log = LoggerFactory.getLogger(ConnectionsRegistry.class);

	private final TimeProvider timeProvider;
	private final long checkTickTimeout;
	private final long maxKeepAliveTime;

	private final Set<ClientConnection> activeClients = Sets.newHashSet();
	private final BlockingDeque<ClientConnection> newClients = Queues.newLinkedBlockingDeque();

	public ConnectionsRegistry(TimeProvider timeProvider) {
		this(timeProvider, TimeUnit.SECONDS.toMillis(180), 100);
	}

	public ConnectionsRegistry(TimeProvider timeProvider, long maxKeepAliveTime, long checkTickTimeout) {
		this.timeProvider = timeProvider;
		this.maxKeepAliveTime = maxKeepAliveTime;
		this.checkTickTimeout = checkTickTimeout;
	}

	public void addNewClient(ClientConnection clientConnection) throws ClientRejectedException {
		boolean result = newClients.offer(clientConnection);
		if(!result) {
			throw new ClientRejectedException("Can not register client");
		}
	}

	public int getActiveClientsCount() {
		return activeClients.size();
	}

	@Override
	public void run() {
		final long currentTime = timeProvider.getCurrentTime();
		List<ClientConnection> newClients = getLatestEvents();
		newClients.forEach(client -> {
			if(!activeClients.contains(client)) {
				log.debug("New client connection {}", client);
				activeClients.add(client);
			} else {
				log.warn("Client already registered {}", client);
			}
		});

		final Iterator<ClientConnection> connectedClients = activeClients.iterator();
		while (connectedClients.hasNext()) {
			final ClientConnection connection = connectedClients.next();

			if(!connection.isValid()) {
				log.info("Client connection broken {}", connection);
				performClientCloseAction(connection::close);
				connectedClients.remove();
			} else if(currentTime - connection.getLastHeartbeat() > maxKeepAliveTime) {
				log.info("Client connection {} timeout after {}", connection, currentTime - connection.getLastHeartbeat());
				performClientCloseAction(connection::close);
				connectedClients.remove();
			}
		}
	}

	@Override
	public void onAfterRun() {
		closeAllConnections();
	}

	@FunctionalInterface
	private interface ClientCloseAction {
		void close() throws IOException;
	}

	private void performClientCloseAction(ClientCloseAction closeAction) {
		try {
			closeAction.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<ClientConnection> getLatestEvents() {
		final List<ClientConnection> eventsToProcess = Lists.newLinkedList();

		ClientConnection newConnection = null;
		try {
			newConnection = newClients.poll(checkTickTimeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) { }

		if(newConnection != null) {
			eventsToProcess.add(newConnection);
		}

		newClients.drainTo(eventsToProcess);
		return eventsToProcess;
	}

	private void closeAllConnections() {
		activeClients.stream().forEach(connection -> performClientCloseAction(connection::close));
		activeClients.clear();
	}
}
