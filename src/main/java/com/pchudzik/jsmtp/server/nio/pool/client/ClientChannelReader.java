package com.pchudzik.jsmtp.server.nio.pool.client;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by pawel on 10.04.14.
 */
class ClientChannelReader extends Reader {
	private final ClientConnection clientConnection;
	private final Charset charset;

	ClientChannelReader(ClientConnection clientConnection, Charset charset) {
		this.clientConnection = clientConnection;
		this.charset = charset;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if(off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
			throw new IndexOutOfBoundsException();
		} else if(len == 0) {
			return 0;
		}

		try {
			final ByteBuffer buffer = ByteBuffer.allocate(len);
			final int readCount = clientConnection.channel().read(buffer);
			if (readCount == -1) {
				return -1;
			}

			final byte [] readBytes = new byte[readCount];

			buffer.rewind();
			buffer.get(readBytes);

			final String readDataString = new String(readBytes, charset);
			final int readDataCount = readDataString.length();
			readDataString.getChars(0, readDataCount, cbuf, off);

			return readDataCount;
		} catch (IOException ex) {
			clientConnection.setBroken(ex);
			throw ex;
		}
	}

	@Override
	public void close() throws IOException {
		//no operation here I don't wand to close socket just to read all it's data
	}
}
