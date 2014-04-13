package com.pchudzik.jsmtp.server.nio.pool;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * User: pawel
 * Date: 12.04.14
 * Time: 13:41
 */
public class ClientConnection {
	private final SelectionKey selectionKey;

	private Throwable brokenReason;

	ClientConnection(SelectionKey selectionKey) {
		this.selectionKey = selectionKey;
	}

	public void close() throws IOException {
		selectionKey.cancel();
		selectionKey.channel().close();
	}

	public boolean isValid() {
		return brokenReason != null || !selectionKey.isValid();
	}

	public long getLastHeartbeat() {
		return 0;
	}

	public void setBroken(Throwable reason) {
		this.brokenReason = reason;
	}

	public SocketChannel channel() {
		return (SocketChannel)selectionKey.channel();
	}
}
