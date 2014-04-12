package com.pchudzik.tmail.jsmtp.server.pool;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by pawel on 10.04.14.
 */
public class ConnectionsAcceptingServer extends Thread implements Closeable {
	private int port = -1;
	private final String host;
	private final ConnectionPool connectionPool;

	private final AtomicBoolean isRunning = new AtomicBoolean(true);

	private ServerSocketChannel serverSocketChannel;
	private ServerSocket serverSocket;
	private Selector serverSelector;

	public ConnectionsAcceptingServer(ConnectionPool connectionPool) {
		this("localhost", selectPort(), connectionPool);
	}

	public ConnectionsAcceptingServer(String host, int port, ConnectionPool connectionPool) {
		this.port = port;
		this.host = host;
		this.connectionPool = connectionPool;
	}

	public int getPort() {
		return port;
	}

	public String getHost() {
		return host;
	}

	@Override
	public synchronized void start() {
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);

			serverSocket = serverSocketChannel.socket();
			serverSocket.bind(new InetSocketAddress(host, port));

			serverSelector = Selector.open();
			serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}

		super.start();
	}

	@Override
	public void run() {
		while (isRunning.get()) {
			try {
				if(serverSelector.select() > 0) {
					Iterator<SelectionKey> it = serverSelector.selectedKeys().iterator();
					while(it.hasNext()) {
						SelectionKey key = it.next();
						if(key.isAcceptable()) {
							ServerSocketChannel channel = (ServerSocketChannel) key.channel();
							connectionPool.registerClient(channel.accept());
						}
						it.remove();
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private static int selectPort() {
		int selectedPort = 49152;
		while(true) {
			try {
				ServerSocket socket = new ServerSocket(selectedPort);
				socket.close();
				break;	//there is port open :)
			} catch (Exception ex) {
				selectedPort++;
			}
		}
		return selectedPort;
	}

	@Override
	public void close() throws IOException {
		isRunning.set(false);
		serverSelector.wakeup();
	}
}
