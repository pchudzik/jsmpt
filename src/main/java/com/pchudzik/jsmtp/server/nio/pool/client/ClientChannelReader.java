package com.pchudzik.jsmtp.server.nio.pool.client;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
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

		clientConnection.heartbeat();

		try {
			final ByteBuffer buffer = ByteBuffer.allocate(len * Character.BYTES);
			final int readCount = clientConnection.channel().read(buffer);
			if (readCount <= -1) {
				clientConnection.close();
				return -1;
			} else if(readCount == 0) {
				//nothing read
				return -1;
			}

			buffer.flip();
			final CharBuffer charBuffer = charset.decode(buffer);
			charBuffer.get(cbuf, off, charBuffer.length());
			charBuffer.flip();

			return charBuffer.length();
		} catch (IOException ex) {
			clientConnection.setBrokenReason(ex);
			throw ex;
		}
	}

	@Override
	public void close() throws IOException {
		//no operation here I don't wand to close socket just to read all it's data
	}
}
