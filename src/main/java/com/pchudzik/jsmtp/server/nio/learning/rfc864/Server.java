package com.pchudzik.jsmtp.server.nio.learning.rfc864;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * User: pawel
 * Date: 05.04.14
 * Time: 11:56
 */
class Server {
	public static final String host = "localhost";
	static final int port = 1919;

	public static void main(String[] args) throws Exception {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		ServerSocket serverSocket = serverSocketChannel.socket();

		serverSocket.bind(new InetSocketAddress(host, port));

		serverSocketChannel.configureBlocking(false);

		Selector selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		while(true) {
			if(selector.select() > 0) {
				for(Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
					SelectionKey key = it.next();
					if(key.isAcceptable()) {
						SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
						System.out.println("accepted connection from " + client);
						client.configureBlocking(false);
						SelectionKey key2 = client.register(selector, SelectionKey.OP_WRITE);

						ByteBuffer buffer = ByteBuffer.wrap("ala ma kota\n".getBytes());
						key2.attach(buffer);
					} else if(key.isWritable()) {
						SocketChannel client = (SocketChannel) key.channel();
						ByteBuffer buffer = (ByteBuffer) key.attachment();
						if(!buffer.hasRemaining()) {
							buffer.rewind();
							buffer.put("alamakota\n".getBytes());
							buffer.flip();
						}
						client.write(buffer);
					}
					it.remove();
				}
			}
		}
	}
}
