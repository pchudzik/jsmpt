package com.pchudzik.jsmtp.server.nio.pool;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.pchudzik.jsmtp.common.RunnableTask;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * User: pawel
 * Date: 06.04.14
 * Time: 18:10
 */
class ConnectionPoolElement implements RunnableTask {
	private static final Logger log = LoggerFactory.getLogger(ConnectionPoolElement.class);

	private final int selectionOperation;
	private final ConnectionPoolConfiguration connectionPoolConfiguration;

	private final ClientHandler clientHandler;
	private final ClientConnectionFactory connectionFactory;

	private final Selector clientSelector;
	private final LinkedBlockingQueue<SocketChannel> incomingConnectionsQueue;

	public ConnectionPoolElement(ConnectionPoolConfiguration connectionPoolConfiguration, ClientConnectionFactory connectionFactory, ClientHandler clientHandler) throws IOException {
		this.selectionOperation = SelectionKey.OP_READ;
		this.connectionPoolConfiguration = connectionPoolConfiguration;
		this.clientHandler = clientHandler;
		this.connectionFactory = connectionFactory;

		this.incomingConnectionsQueue = Queues.newLinkedBlockingQueue(connectionPoolConfiguration.getNewClientsQueueSize());
		this.clientSelector = Selector.open();
	}

	public void registerClient(SocketChannel newClient) throws ClientRejectedException {
		try {
			final boolean accepted = incomingConnectionsQueue.offer(
					newClient,
					connectionPoolConfiguration.getNewClientRegisterTimeout(),
					TimeUnit.MILLISECONDS);
			if(!accepted) {
				throw new ClientRejectedException("Client not accepted by thread " + Thread.currentThread().getName() +
						" waiting queue size " + incomingConnectionsQueue.size());
			} else {
				log.debug("New client registered and waiting for processing");
				clientSelector.wakeup();
			}
		} catch (InterruptedException e) {
			throw new ClientRejectedException("Client not accepted", e);
		}
	}

	@Override
	public void onClose() {
		clientSelector.wakeup();
	}

	@Override
	public void run() {
		int selectedKeys = 0;
		try {
			selectedKeys = clientSelector.select(1000L);
		} catch (IOException e) {
			log.warn("Can not select new client");
		}

		checkAndRegisterNewClients();

		if(selectedKeys > 0) {
			processIncomingData(clientSelector.selectedKeys().iterator());
		}
	}

	private void checkAndRegisterNewClients() {
		if(!incomingConnectionsQueue.isEmpty()) {
			List<SocketChannel> newClients = Lists.newLinkedList();
			incomingConnectionsQueue.drainTo(newClients);

			performNewClientsRegistration(newClients);
		}
	}

	protected void performNewClientsRegistration(List<SocketChannel> newClients) {
		newClients.forEach(channel -> {
			try {
				channel.configureBlocking(false);
				final SelectionKey selectionKey = channel.register(
						clientSelector,
						selectionOperation);
				try {
					final ClientConnection clientConnection = connectionFactory.newConnection(selectionKey);

					selectionKey.attach(clientConnection);

					try {
						clientHandler.onNewClientConnection(clientConnection);
						log.debug("New clients registered {}", clientConnection);
					} catch (Exception ex) {
						log.info("Client handler failed to process new client registration", ex);
						clientConnection.setBrokenReason(ex);
					}
				} catch (ClientRejectedException ex) {
					log.info("Client not accepted", ex);
					selectionKey.channel().close();
					selectionKey.cancel();
				}
			} catch (IOException e) {
				log.debug("Client connection closed", e);
			}
		});
	}

	protected void processIncomingData(Iterator<SelectionKey> keyIterator) {
		while (keyIterator.hasNext()) {
			final SelectionKey selectionKey = keyIterator.next();
			try {
				processSingleClientData(selectionKey);
			} finally {
				keyIterator.remove();
			}
		}
	}

	private void processSingleClientData(SelectionKey selectionKey) {
		final ClientConnection clientConnection = (ClientConnection) selectionKey.attachment();
		try {
			clientHandler.processClient(clientConnection);
		} catch (Exception ex) {
			log.warn("Unable to process client data", ex);
			clientConnection.setBrokenReason(ex);
		}
	}
}
