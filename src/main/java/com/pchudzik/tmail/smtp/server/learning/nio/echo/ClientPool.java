package com.pchudzik.tmail.smtp.server.learning.nio.echo;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: pawel
 * Date: 05.04.14
 * Time: 12:52
 */
public class ClientPool {
	final List<SingleClientThread> clientThreads = Lists.newArrayList();
	final Random random = new Random();

	public ClientPool(int poolSize) throws IOException {
		for(int i = 0 ; i < poolSize; i++) {
			clientThreads.add(new SingleClientThread());
		}
	}

	public void register(SocketChannel client) throws Exception {
		SingleClientThread clientThread = selectRandomThread();
		clientThread.register(client);
	}

	private SingleClientThread selectRandomThread() {
		return clientThreads.get(random.nextInt(clientThreads.size()));
	}

	public void start() {
		for(SingleClientThread thread : clientThreads) {
			thread.start();
		}
	}

	private class SingleClientThread extends Thread {
		private AtomicLong receivedSize = new AtomicLong(0);

		private final Selector clientSelector;
		private final LinkedBlockingQueue<SocketChannel> queue = new LinkedBlockingQueue<>(1000);


		public SingleClientThread() throws IOException {
			this.clientSelector = Selector.open();
		}

		public void register(SocketChannel client) throws Exception {
			queue.put(client);
			clientSelector.wakeup();
		}

		@Override
		public void run() {
			while(true) {
				checkForClientData();
				registerNewClients();
				Iterator<SelectionKey> clients = clientSelector.selectedKeys().iterator();
				while (clients.hasNext()) {
					SelectionKey key = clients.next();
					if(key.isReadable()) {
						readDataFromClient(key);
					} else if(key.isWritable()) {
						processClientResponse(key);
					}
					clients.remove();
				}
			}
		}

		private void registerNewClients() {
			List<SocketChannel> currentQueue = new LinkedList<>();
			queue.drainTo(currentQueue);
			for(SocketChannel channel : currentQueue) {
				try {
					channel.register(clientSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, new ClientContext(channel));
				} catch (ClosedChannelException e) {
					e.printStackTrace();
				}
			}
		}

		private void processClientResponse(SelectionKey key) {
			try {
				SocketChannel client = (SocketChannel) key.channel();
				ClientContext clientContext = (ClientContext) key.attachment();

				if(!clientContext.commandHistory.isEmpty()) {
					String lastCmd = clientContext.commandHistory.pop();
					String reversed = reverse(lastCmd);
					client.write(ByteBuffer.wrap(("response: " + reversed + "\n\r").getBytes()));
				}
			} catch (Exception ex) {
				try {
					key.channel().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				key.cancel();
			}
		}

		private String reverse(String lastCmd) {
			StringBuilder sb = new StringBuilder();
			for(int i = lastCmd.length() - 1; i >= 0; i--) {
				sb.append(lastCmd.charAt(i));
			}
			return sb.toString();
		}

		private void readDataFromClient(SelectionKey key) {
			try {
				ClientContext clientContext = (ClientContext) key.attachment();
				SocketChannel client = (SocketChannel) key.channel();
				ByteBuffer buffer = ByteBuffer.allocate(128);
				StringBuilder clientQuery = new StringBuilder();
				int read = 0;
				while ((read = client.read(buffer)) > 0) {
					receivedSize.addAndGet(read);
					byte [] receivedData = new byte[read];
					buffer.rewind();
					buffer.get(receivedData);
					clientQuery.append(new String(receivedData, Charset.forName("utf8")));
					buffer.clear();
				}
				if(!clientQuery.toString().trim().isEmpty()) {
//					System.out.println("received " + clientQuery.toString());
					clientContext.commandHistory.push(clientQuery.toString().trim());
				}

				if(receivedSize.get() % 10_000 == 0) {
					System.out.println("received " + receivedSize.get());
				}
			} catch (Exception ex) {
				try {
					key.channel().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				key.cancel();
			}
		}

		private void checkForClientData() {
			try {
				clientSelector.select();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ClientContext {
		final SocketChannel client;
		final LinkedList<String> commandHistory = Lists.newLinkedList();

		public ClientContext(SocketChannel client) {
			this.client = client;
		}
	}
}
