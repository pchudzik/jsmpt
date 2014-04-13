package com.pchudzik.jsmtp.server.nio.learning.rfc864;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

/**
 * User: pawel
 * Date: 05.04.14
 * Time: 11:49
 */
class Client {
	public static void main(String[] args) throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocate(74);
		final SocketChannel client = SocketChannel.open(new InetSocketAddress(Server.host, Server.port));

		WritableByteChannel output = Channels.newChannel(System.out);
		System.out.println("connected");

		while (client.read(buffer) != -1) {
			buffer.flip();
//			output.write(buffer);
			buffer.clear();
		}
	}
}
