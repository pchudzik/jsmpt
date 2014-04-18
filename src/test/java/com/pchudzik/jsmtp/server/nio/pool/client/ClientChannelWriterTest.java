package com.pchudzik.jsmtp.server.nio.pool.client;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * User: pawel
 * Date: 13.04.14
 * Time: 10:06
 */
public class ClientChannelWriterTest {
	private final Charset utf8charset = Charset.forName("UTF-8");
	private TestClientConnectionFactory connectionFactory;

	private SelectionKey selectionKeyMock;
	private SocketChannel socketChannel;
	private ClientConnection clientConnection;

	@BeforeClass
	public void setupClientConnectionFactory() {
		connectionFactory = new TestClientConnectionFactory();
	}

	@BeforeMethod
	public void setupClientConnection() throws Exception {
		socketChannel = connectionFactory.mockSocketChannel();
		selectionKeyMock = connectionFactory.mockSelectionKeyWithChannel(socketChannel);
		clientConnection = connectionFactory.newConnection(selectionKeyMock);
	}

	@Test
	public void shouldWriteDataToChannel() throws IOException {
		final String anyString = "ala ma kota";
		final ByteBuffer sendStringBuffer = ByteBuffer.wrap(anyString.getBytes(utf8charset));

		writeDataToChannel(anyString);

		verify(socketChannel).write(sendStringBuffer);
	}

	@Test
	public void shouldUpdateHeartbeatOnSuccessWrite() throws IOException {
		final long now = 100L;
		connectionFactory.getTimeProvider().setNow(now);

		writeDataToChannel("any string");

		assertThat(clientConnection.getLastHeartbeat()).isEqualTo(now);
	}

	@Test
	public void shouldMarkBrokenOnError() throws Exception {
		final IOException exception = new IOException("exception");
		when(socketChannel.write(any(ByteBuffer.class))).thenThrow(exception);

		writeDataToChannel("any string");

		assertThat(clientConnection.isValid()).isTrue();
	}

	private void writeDataToChannel(String data) throws IOException {
		new ClientChannelWriter(clientConnection, utf8charset)
				.write(data);
	}
}
