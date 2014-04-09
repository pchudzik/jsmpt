package com.pchudzik.tmail.jsmtp.server.pool;

import com.pchudzik.tmail.jsmtp.server.ClientRejectedException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * User: pawel
 * Date: 08.04.14
 * Time: 16:21
 */
public class ServerThreadTest {
	private static final boolean STARTED = true;
	private static final int anySelectionOperation = SelectionKey.OP_ACCEPT;

	private ServerThread serverThread;

	@AfterMethod
	public void shutdownThread() throws IOException {
		if(serverThread != null) {
			serverThread.shutdown();
		}
	}

	@Test
	public void shouldRejectClientsIfIncomingConnectionsQueueExceeded() throws Exception {
		serverThread = doNothingServer(STARTED);

		serverThread.registerClient(mock(SocketChannel.class));

		catchException(serverThread).registerClient(mock(SocketChannel.class));

		assertThat((Exception)caughtException())
				.isInstanceOf(ClientRejectedException.class);
	}

	@Test
	public void shouldRejectIncomingConnectionsWhenClosed() {

	}

	@Test
	public void shouldHandleIncomingConnectionFromNewClient() {

	}

	@Test
	public void shouldRemoveDisconnectedClients() {

	}

	@Test
	public void shouldDisconnectAllClientsOnShutdown() {

	}

	@Test
	public void clientHandlerExceptionOnProcessClientShouldNotDestroyThread() {

	}

	@Test
	public void clientHandlerExceptionOnNewClientShouldNotDestroyThread() {

	}

	private ServerThread doNothingServer(boolean startThread) throws IOException {
		return new ServerThread(
				anySelectionOperation,
				new ServerThreadConfiguration("accept thread")
						.setNewClientsQueueSize(1)
						.setNewClientRegisterTimeout(1),
				handler -> {
				}
		) {
			{
				if(startThread) {
					//fake thread to be running
					isWorking = true;
				}
			}

			@Override
			protected void performNewClientsRegistration(List<SocketChannel> newClients) {
				//do nothing
			}
		};
	}
}
