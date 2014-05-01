package com.pchudzik.jsmtp.server.nio.pool;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import com.pchudzik.jsmtp.common.RunnableTask;

/**
 * Created by pawel on 10.04.14.
 */
public class ConnectionsAcceptingServer implements RunnableTask {
	private int port = -1;
	private final String host;
	private final ConnectionPool connectionPool;

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
	public void onBeforeRun() {
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
	}

	@Override
	public void run() {
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

	@Override
	public void onClose() {
		serverSelector.wakeup();
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
}
