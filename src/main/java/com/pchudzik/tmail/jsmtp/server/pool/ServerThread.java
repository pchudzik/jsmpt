package com.pchudzik.tmail.jsmtp.server.pool;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.pchudzik.tmail.jsmtp.server.ClientRejectedException;
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
public class ServerThread extends Thread {
	private final int selectionOperation;
	private final ServerThreadConfiguration serverThreadConfiguration;
	private final ClientHandler clientHandler;

	private final Logger log;

	protected volatile boolean isWorking = false;

	private final Selector clientSelector;
	private final LinkedBlockingQueue<SocketChannel> incomingConnectionsQueue;

	public ServerThread(int selectionOperation, ServerThreadConfiguration serverThreadConfiguration, ClientHandler clientHandler) throws IOException {
		this.selectionOperation = selectionOperation;
		this.serverThreadConfiguration = serverThreadConfiguration;
		this.clientHandler = clientHandler;

		this.log = LoggerFactory.getLogger(getClass().getSimpleName() + " - " + serverThreadConfiguration.getThreadName());
		this.incomingConnectionsQueue = Queues.newLinkedBlockingQueue(serverThreadConfiguration.getNewClientsQueueSize());
		this.clientSelector = Selector.open();
	}

	public void registerClient(SocketChannel newClient) throws ClientRejectedException {
		ensureThreadIsRunning();

		try {
			final boolean accepted = incomingConnectionsQueue.offer(newClient, serverThreadConfiguration.getNewClientRegisterTimeout(), TimeUnit.MILLISECONDS);
			if(!accepted) {
				throw new ClientRejectedException("Client not accepted by thread " + getName() + " waiting queue size " + incomingConnectionsQueue.size());
			} else {
				try {
					clientHandler.onNewClient(newClient);
				} catch (Exception ex) {
					log.warn("Client handler failed to process new client registration", ex);
				}
				clientSelector.wakeup();
			}
		} catch (InterruptedException e) {
			throw new ClientRejectedException("Client not accepted", e);
		}
	}

	private void ensureThreadIsRunning() throws ClientRejectedException {
		if(!isWorking) {
			throw new ClientRejectedException("Thread " + getName() + " already stopped");
		}
	}

	public void shutdown() throws IOException {
		isWorking = false;
		clientSelector.close();
		clientSelector.wakeup();
	}

	public boolean isWorking() {
		return isWorking;
	}

	@Override
	public void run() {
		isWorking = true;
		while (isWorking) {
			try {
				clientSelector.select(1000L);
			} catch (IOException e) {
				log.warn("Can not select new client");
			}

			checkAndRegisterNewClients();

			processIncomingData(clientSelector.selectedKeys().iterator());
		}
	}

	private void checkAndRegisterNewClients() {
		List<SocketChannel> newClients = Lists.newLinkedList();
		incomingConnectionsQueue.drainTo(newClients);

		performNewClientsRegistration(newClients);
	}

	protected void performNewClientsRegistration(List<SocketChannel> newClients) {
		newClients.forEach(channel -> {
			try {
				channel.configureBlocking(false);
				channel.register(
						clientSelector,
						selectionOperation,
						new ClientContext());
			} catch (IOException e) {
				log.debug("Client connection closed", e);
			}
		});
	}

	protected void processIncomingData(Iterator<SelectionKey> keyIterator) {
		while (keyIterator.hasNext()) {
			final SelectionKey selectionKey = keyIterator.next();
			try {
				clientHandler.processClient(selectionKey);
			} catch (Exception ex) {
				log.warn("Unable to process client dat", ex);
			} finally {
				keyIterator.remove();
			}
		}
	}
}
