package com.pchudzik.tmail.jsmtp.server.nio.echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * User: pawel
 * Date: 05.04.14
 * Time: 12:31
 */
class Server {
	private final ClientPool clientPool;
	private final ServerSocketChannel serverSocketChannel;

	private final int serverListeningPort;

	private boolean running;
	private Selector serverSelector;
	private ServerSocket serverSocket;


	public Server(int port, int threadPoolsSize) throws IOException {
		this.serverListeningPort = port;
		this.serverSocketChannel = ServerSocketChannel.open();

		this.clientPool = new ClientPool(threadPoolsSize);
	}

	public static void main(String[] args) throws Exception {
		new Server(2020, 2).start();
	}

	public void start() throws Exception {
		serverSocketChannel.configureBlocking(false);

		serverSocket = serverSocketChannel.socket();
		serverSocket.bind(new InetSocketAddress(serverListeningPort));

		serverSelector = Selector.open();
		serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);

		running = true;
		clientPool.start();
		processIncomingClients();
	}

	private void processIncomingClients() throws Exception {
		while (running) {
			checkForIncommingClients();

			Iterator<SelectionKey> clientList = serverSelector.selectedKeys().iterator();
			while (clientList.hasNext()) {
				SelectionKey clientSelectionKey = clientList.next();
				if(clientSelectionKey.isAcceptable()) {
					final ServerSocketChannel server = (ServerSocketChannel) clientSelectionKey.channel();
					final SocketChannel client = server.accept();
					client.configureBlocking(false);
					clientPool.register(client);
				}
				clientList.remove();
			}
		}
	}

	private void checkForIncommingClients() {
		try {
			serverSelector.select();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
