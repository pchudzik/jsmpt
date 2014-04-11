package com.pchudzik.tmail.jsmtp.server.pool;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.pchudzik.tmail.jsmtp.server.common.TimeProvider;

import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * User: pawel
 * Date: 10.04.14
 * Time: 18:47
 */
class ConnectionsRegistry extends Thread {
	private volatile boolean isRunning = true;

	private final TimeProvider timeProvider;
	private final long checkTickTimeout;
	private final long maxKeepAliveTime;

	private final List<SelectionData> activeClients = Lists.newLinkedList();

	private final BlockingDeque<ClientEvent> clientEvents = Queues.newLinkedBlockingDeque();

	public ConnectionsRegistry(TimeProvider timeProvider) {
		this(timeProvider, TimeUnit.SECONDS.toMillis(180), 100);
	}

	public ConnectionsRegistry(TimeProvider timeProvider, long maxKeepAliveTime, long checkTickTimeout) {
		this.timeProvider = timeProvider;
		this.maxKeepAliveTime = maxKeepAliveTime;
		this.checkTickTimeout = checkTickTimeout;
	}

	public void addNewClient(SelectionKey selectionKey) {
		clientEvents.offer(new ClientEvent(selectionKey, ClientStatus.NEW));
	}

	public void clientConnectionError(SelectionKey selectionKey) {
		clientEvents.offer(new ClientEvent(selectionKey, ClientStatus.BROKEN));
	}

	public void onClientHeartbeat(SelectionKey selectionKey) {
		clientEvents.offer(new ClientEvent(selectionKey, ClientStatus.HEARTBEAT));
	}

	public void shutdown() {
		isRunning = false;
	}

	public int getActiveClientsCount() {
		return activeClients.size();
	}

	@Override
	public void run() {
		while(isRunning) {
			updateClientData();
		}

		closeAllConnections();
	}

	protected void updateClientData() {
		final List<ClientEvent> eventsToProcess = getLatestEvents();
		if(eventsToProcess.isEmpty()) {
			return;
		}

		final Set<SelectionKey> newClients = Sets.newHashSet();
		final Set<SelectionKey> brokenClients = Sets.newHashSet();
		final Set<SelectionKey> refreshedClients = Sets.newHashSet();

		clientEvents.stream()
				.forEach(event -> {
					switch (event.getClientStatus()) {
						case NEW: newClients.add(event.getSelectionKey()); break;
						case BROKEN: brokenClients.add(event.getSelectionKey()); break;
						case HEARTBEAT: refreshedClients.add(event.getSelectionKey()); break;
					}
				});

		final long currentTime = timeProvider.getCurrentTime();
		final Iterator<SelectionData> connectedClients = activeClients.iterator();
		while (connectedClients.hasNext()) {
			SelectionData data = connectedClients.next();
			if(brokenClients.contains(data.getSelectionKey()) || !data.isValid()) {
				connectedClients.remove();
				closeBrokenClient(data.getSelectionKey());
			}

			if(currentTime - data.getLastActiveTime() > maxKeepAliveTime) {
				connectedClients.remove();
				closeTimeoutConnection(data.getSelectionKey());
			}

			if(refreshedClients.contains(data.getSelectionKey())) {
				data.heartbeat(currentTime);
			}
		}

		activeClients.addAll(newClients.stream()
				.map(selector -> new SelectionData(selector, currentTime))
				.collect(Collectors.<SelectionData>toList()));
	}

	private List<ClientEvent> getLatestEvents() {
		final List<ClientEvent> eventsToProcess = Lists.newLinkedList();

		ClientEvent event = null;
		try {
			event = clientEvents.poll(checkTickTimeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) { }

		if(event != null) {
			eventsToProcess.add(event);
		}

		clientEvents.drainTo(eventsToProcess);
		return eventsToProcess;
	}

	private void closeAllConnections() {

	}

	private void closeTimeoutConnection(SelectionKey selectionKey) {

	}

	private void closeBrokenClient(SelectionKey selectionKey) {

	}

	private static class SelectionKeyAware {
		private final SelectionKey selectionKey;

		private SelectionKeyAware(SelectionKey selectionKey) {
			this.selectionKey = selectionKey;
		}

		public boolean isValid() {
			return selectionKey.isValid();
		}

		public SelectionKey getSelectionKey() {
			return selectionKey;
		}
	}

	private static enum ClientStatus {
		BROKEN, NEW, HEARTBEAT
	}

	private static class ClientEvent extends SelectionKeyAware {
		private final ClientStatus clientStatus;
		private ClientEvent(SelectionKey selectionKey, ClientStatus clientStatus) {
			super(selectionKey);
			this.clientStatus = clientStatus;
		}

		private ClientStatus getClientStatus() {
			return clientStatus;
		}
	}

	private static class SelectionData extends SelectionKeyAware{
		private long lastActiveTime;

		private SelectionData(SelectionKey selectionKey, long currentTime) {
			super(selectionKey);
			this.lastActiveTime = currentTime;
		}

		public void heartbeat(long currentTime) {
			lastActiveTime = currentTime;
		}

		public long getLastActiveTime() {
			return lastActiveTime;
		}
	}
}
