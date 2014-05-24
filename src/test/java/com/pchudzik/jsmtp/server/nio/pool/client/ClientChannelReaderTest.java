package com.pchudzik.jsmtp.server.nio.pool.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.apache.commons.lang.mutable.MutableInt;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created by pawel on 17.04.14.
 */
public class ClientChannelReaderTest {
	public static final Charset utf8Charset = Charset.forName("utf-8");
	private final TestClientConnectionFactory clientConnectionFactory = new TestClientConnectionFactory();

	SelectionKey selectionKeyMock;
	SocketChannel socketChannelMock;

	@BeforeMethod
	public void setupSelectionKey() {
		socketChannelMock = clientConnectionFactory.mockSocketChannel();
		selectionKeyMock = clientConnectionFactory.mockSelectionKeyWithChannel(socketChannelMock);
	}

	@DataProvider(name = "offsetLengthParameters") Object[][] offsetParameters() {
		return new Object[][] {
				{10, 11, 1},	//offset bigger then buffer
				{10, 6, 5},		//offset with length bigger then buffer
				{10, -6, 7},	//negative offset
				{10, 0, -2},	//negative length
				{10, 0, 11},	//length bigger then buffer
		};
	}

	@Test(dataProvider = "offsetLengthParameters")
	public void shouldThrowExceptionOnInvalidOffset(int bufferSize, int offset, int length) throws Exception {
		ClientChannelReader reader = new ClientChannelReader(
				clientConnectionFactory.newConnection(selectionKeyMock),
				utf8Charset);

		catchException(reader).read(new char[bufferSize], offset, length);

		assertThat((Exception)caughtException())
				.isInstanceOf(IndexOutOfBoundsException.class);
	}

	@Test
	//some utf characters are longer then one byte.
	//for example char = 'ą' is two bytes.
	public void shouldFillBufferWithUtf8Data() throws Exception {
		final String withUtfString = "ąłó";
		mockReaderToReturn(withUtfString + " other part");

		final ClientChannelReader reader = new ClientChannelReader(
				clientConnectionFactory.newConnection(selectionKeyMock),
				utf8Charset);

		final char [] buffer = new char[withUtfString.length()];
		reader.read(buffer);

		assertThat(new String(buffer)).isEqualTo(withUtfString);
	}

	@Test
	public void shouldCloseClientConnectionOnBrokenChannel() throws Exception {
		final ClientConnection clientConnection = mock(ClientConnection.class);

		when(socketChannelMock.read(any(ByteBuffer.class))).thenReturn(-1);
		when(clientConnection.channel()).thenReturn(socketChannelMock);

		new ClientChannelReader(clientConnection, utf8Charset)
				.read();

		verify(clientConnection).close();
	}

	@Test
	public void shouldFillBufferWithSimpleData() throws Exception {
		final String simpleString = "ala ma kota";
		mockReaderToReturn(simpleString);

		final ClientChannelReader reader = new ClientChannelReader(
				clientConnectionFactory.newConnection(selectionKeyMock),
				utf8Charset);

		final char [] buffer = new char[simpleString.length()];
		reader.read(buffer);

		assertThat(new String(buffer)).isEqualTo(simpleString);
	}

	private void mockReaderToReturn(String resultString) throws IOException {
		final MutableInt bytesPosition = new MutableInt(0);

		final byte [] fullClientData = resultString.getBytes(utf8Charset);

		when(socketChannelMock.read(any(ByteBuffer.class))).thenAnswer(invocation -> {
			final ByteBuffer buffer = (ByteBuffer) invocation.getArguments()[0];
			final int initialPosition = bytesPosition.intValue();

			int length = initialPosition + buffer.limit();
			if(length > fullClientData.length) {
				length = fullClientData.length;
			}

			byte [] result = Arrays.copyOfRange(
					fullClientData,
					initialPosition,
					length);

			buffer.put(result);
			bytesPosition.add(result.length);

			return result.length;
		});
	}
}
