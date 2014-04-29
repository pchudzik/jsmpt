package com.pchudzik.jsmtp.server.nio.pool.client;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

class ClientChannelWriter extends Writer {
	private final ClientConnection clientConnection;
	private final Charset charset;

	ClientChannelWriter(ClientConnection clientConnection, Charset charset) {
		this.clientConnection = clientConnection;
		this.charset = charset;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		try {
			final SocketChannel socketChannel = clientConnection.channel();
			final ByteBuffer byteBuffer = charset.encode(CharBuffer.wrap(cbuf, off, len));
			socketChannel.write(byteBuffer);
			clientConnection.heartbeat();
		} catch (IOException ex) {
			clientConnection.setBrokenReason(ex);
		}
	}

	@Override
	public void flush() throws IOException { }

	@Override
	public void close() throws IOException { }
}