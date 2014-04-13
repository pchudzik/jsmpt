package com.pchudzik.jsmtp.server.nio.pool;

import com.pchudzik.jsmtp.common.TimeProvider;

import java.io.IOException;
import java.io.Writer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * User: pawel
 * Date: 12.04.14
 * Time: 13:41
 */
public class ClientConnection {
	private static final String defaultEncoding = "UTF-8";

	private final TimeProvider timeProvider;
	private final SelectionKey selectionKey;

	private Throwable brokenReason;
	private volatile long heartbeat;

	ClientConnection(TimeProvider timeProvider, SelectionKey selectionKey) {
		this.timeProvider = timeProvider;
		this.selectionKey = selectionKey;
	}

	public void close() throws IOException {
		selectionKey.cancel();
		selectionKey.channel().close();
	}

	public boolean isValid() {
		return brokenReason != null || !selectionKey.isValid();
	}

	void heartbeat() {
		this.heartbeat = timeProvider.getCurrentTime();
	}

	public long getLastHeartbeat() {
		return heartbeat;
	}

	public void setBroken(Throwable reason) {
		this.brokenReason = reason;
	}

	SocketChannel channel() {
		return (SocketChannel)selectionKey.channel();
	}

	public Writer getWriter(String charsetName) {
		return new ClientChannelWriter(this, Charset.forName(charsetName));
	}

	public Writer getWriter() {
		return getWriter(defaultEncoding);
	}
}
