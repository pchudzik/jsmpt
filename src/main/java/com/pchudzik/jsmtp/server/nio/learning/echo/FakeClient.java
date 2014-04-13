package com.pchudzik.jsmtp.server.nio.learning.echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * User: pawel
 * Date: 05.04.14
 * Time: 21:43
 */
public class FakeClient {
	public static void main(String[] args) throws IOException, InterruptedException {

		for(int i = 0 ; i < 10 ; i++) {
			new ClientThread(i).start();
		}
	}

	private static class ClientThread extends Thread {
		final int threadId;

		private ClientThread(int threadId) {
			this.threadId = threadId;
		}

		@Override
		public void run() {
			try {
				final SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 2020));
				while (true) {
					client.write(ByteBuffer.wrap((threadId + " " + System.currentTimeMillis() + "\n\r").getBytes()));

					ByteBuffer response = ByteBuffer.allocate(128);
					int read = 0;
//					while((read = client.read(response)) > 0) {
						byte [] responeData = new byte[read];
						response.get(responeData);
						String  rspStr = new String(responeData);
						System.out.print(rspStr);
						response.clear();
//					}
				}
			} catch (Exception e) {}
		}
	}
}
