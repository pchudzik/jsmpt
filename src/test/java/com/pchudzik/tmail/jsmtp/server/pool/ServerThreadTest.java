package com.pchudzik.tmail.jsmtp.server.pool;

import com.pchudzik.tmail.jsmtp.server.ClientRejectedException;
import com.pchudzik.tmail.jsmtp.server.pool.helper.ConnectionsAcceptingServer;
import com.pchudzik.tmail.jsmtp.server.pool.helper.SocketChannelDataReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.mutable.MutableObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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
		serverThread = doNothingServer(STARTED, doNothingHandler());

		serverThread.registerClient(mock(SocketChannel.class));

		catchException(serverThread).registerClient(mock(SocketChannel.class));

		assertThat((Exception)caughtException())
				.isInstanceOf(ClientRejectedException.class);
	}

	@Test
	public void shouldRejectIncomingConnectionsWhenNotStarted() throws Exception {
		serverThread = doNothingServer(!STARTED, doNothingHandler());

		catchException(serverThread).registerClient(mock(SocketChannel.class));

		assertThat((Exception)caughtException())
				.isInstanceOf(ClientRejectedException.class);
	}

	@Test
	public void shouldRejectIncomingConnectionsWhenClosed() throws Exception {
		serverThread = doNothingServer(STARTED, doNothingHandler());
		serverThread.shutdown();

		catchException(serverThread).registerClient(mock(SocketChannel.class));

		assertThat((Exception)caughtException())
				.isInstanceOf(ClientRejectedException.class);
	}

	@Test
	public void shouldHandleIncomingConnectionFromNewClient() throws Exception {
		final String anyString = "ala ma kota";
		final MutableObject receivedString = new MutableObject(null);
		final Semaphore receivedDataSemaphore = new Semaphore(1);
		receivedDataSemaphore.drainPermits();	//no go with test until data is received

		serverThread = new ServerThread(SelectionKey.OP_READ, new ServerThreadConfiguration("reading server"), handler -> {
			try (Reader userDataReader = new SocketChannelDataReader((SocketChannel) handler.channel())) {
				receivedString.setValue(IOUtils.toString(userDataReader));
				receivedDataSemaphore.release();	//synchronize data receiving between threads
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});

		try (ConnectionsAcceptingServer connectionsAcceptingServer = new ConnectionsAcceptingServer(serverThread)) {
			serverThread.start();
			connectionsAcceptingServer.start();

			writeDataToServer(
					connectionsAcceptingServer.getHost(),
					connectionsAcceptingServer.getPort(),
					anyString);

			receivedDataSemaphore.tryAcquire(1_000L, TimeUnit.MILLISECONDS);

			assertThat((String) receivedString.getValue())
					.isEqualTo(anyString);
		}
	}

	@Test
	public void shouldRemoveDisconnectedClients() {

	}

	@Test
	public void shouldDisconnectAllClientsOnShutdown() {

	}

	@Test
	public void clientHandlerExceptionOnProcessClientShouldNotDestroyThread() throws Exception {
		serverThread = doNothingServer(STARTED, failingClientHandler());

		catchException(serverThread).registerClient(mock(SocketChannel.class));

		assertThat((Exception)caughtException()).isNull();
	}

	@Test
	public void clientHandlerExceptionOnNewClientShouldNotDestroyThread() throws Exception {
		ClientHandler failingClient = new ClientHandler() {
			@Override
			public void onNewClient(SocketChannel newClient) {
				throw new RuntimeException();
			}

			@Override
			public void processClient(SelectionKey selectionKey) {
				//nothing
			}
		};

		serverThread = new ServerThread(
				SelectionKey.OP_WRITE,
				new ServerThreadConfiguration("anyName"),
				failingClient);

		try (ConnectionsAcceptingServer connectionsAcceptingServer = new ConnectionsAcceptingServer(serverThread)) {
			serverThread.start();
			connectionsAcceptingServer.start();

			writeDataToServer(
					connectionsAcceptingServer.getHost(),
					connectionsAcceptingServer.getPort(),
					"any content");

			//no exception
		}
	}

	private ClientHandler failingClientHandler() {
		return new ClientHandler() {
			@Override
			public void processClient(SelectionKey selectionKey) {
				throw new RuntimeException();
			}

			@Override
			public void onNewClient(SocketChannel newClient) {
				throw new RuntimeException();
			}
		};
	}

	private void writeDataToServer(String host, int port, String data) throws Exception {
		try(Socket clientSocket = new Socket(host, port)) {
			OutputStream os = clientSocket.getOutputStream();
			os.write(data.getBytes());
			os.flush();
		}
	}

	private ClientHandler doNothingHandler() {
		return handler -> {};
	}

	private ServerThread doNothingServer(boolean startThread, ClientHandler clientHandler) throws IOException {
		return new DoNothingServerThread(
				anySelectionOperation,
				new ServerThreadConfiguration("accept thread")
						.setNewClientsQueueSize(1)
						.setNewClientRegisterTimeout(1),
				clientHandler
		).setStarted(startThread);
	}

	private static class DoNothingServerThread extends ServerThread {
		public DoNothingServerThread(int selectionOperation, ServerThreadConfiguration serverThreadConfiguration, ClientHandler clientHandler) throws IOException {
			super(selectionOperation, serverThreadConfiguration, clientHandler);
		}

		public DoNothingServerThread setStarted(boolean started) {
			isWorking = started;
			return this;
		}

		@Override
		protected void performNewClientsRegistration(List<SocketChannel> newClients) {
			//do nothing
		}
	}
}
