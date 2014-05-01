package com.pchudzik.jsmtp.server.nio.pool.client;

import com.pchudzik.jsmtp.common.TimeProvider;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
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

	@Getter private final ClientContext clientContext;
	@Setter private volatile Throwable brokenReason;
	@Getter private volatile long lastHeartbeat;

	ClientConnection(TimeProvider timeProvider, SelectionKey selectionKey, ClientContext clientContext) {
		this.timeProvider = timeProvider;
		this.selectionKey = selectionKey;
		this.clientContext = clientContext;
		heartbeat();
	}

	public void close() throws IOException {
		selectionKey.cancel();
		selectionKey.channel().close();
	}

	public boolean isValid() {
		return brokenReason == null && selectionKey.isValid();
	}

	void heartbeat() {
		this.lastHeartbeat = timeProvider.getCurrentTime();
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

	public BufferedReader getReader(String charset) {
		return new BufferedReader(new ClientChannelReader(this, Charset.forName(charset)));
	}

	public BufferedReader getReader() {
		return getReader(defaultEncoding);
	}
}
