package com.pchudzik.tmail.jsmtp.server.pool.helper;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by pawel on 10.04.14.
 */
public class SocketChannelDataReader extends Reader {
	private final SocketChannel socketChannel;
	private final String encoding;

	public SocketChannelDataReader(SocketChannel socketChannel) {
		this(socketChannel, "utf-8");
	}

	public SocketChannelDataReader(SocketChannel socketChannel, String encoding) {
		this.socketChannel = socketChannel;
		this.encoding = encoding;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if(off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
			throw new IndexOutOfBoundsException();
		} else if(len == 0) {
			return 0;
		}

		final ByteBuffer buffer = ByteBuffer.allocate(len);
		final int readCount = socketChannel.read(buffer);
		if(readCount == -1) {
			return -1;
		}

		final byte [] readBytes = new byte[readCount];

		buffer.rewind();
		buffer.get(readBytes);

		final String readDataString = new String(readBytes, encoding);
		final int readDataCount = readDataString.length();
		readDataString.getChars(0, readDataCount, cbuf, off);

		return readDataCount;
	}

	@Override
	public void close() throws IOException {
		//no operation here I don't wand to close socekt just to read all it's data
	}
}
