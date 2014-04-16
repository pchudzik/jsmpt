package com.pchudzik.jsmtp.server.nio.pool;

import com.pchudzik.jsmtp.common.FakeTimeProvider;
import com.pchudzik.jsmtp.common.StoppableThread;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnectionFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.mutable.MutableObject;
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
public class ConnectionPoolTest {
	private static final int anySelectionOperation = SelectionKey.OP_ACCEPT;

	private ConnectionPool connectionPool;
	private ClientConnectionFactory clientConnectionFactory = mock(ClientConnectionFactory.class);

	@Test
	public void shouldRejectClientsIfIncomingConnectionsQueueExceeded() throws Exception {
		connectionPool = doNothingConnectionPool(doNothingHandler());

		connectionPool.registerClient(mock(SocketChannel.class));

		catchException(connectionPool).registerClient(mock(SocketChannel.class));

		assertThat((Exception)caughtException())
				.isInstanceOf(ClientRejectedException.class);
	}

	@Test
	public void shouldHandleIncomingConnectionFromNewClient() throws Exception {
		final String anyString = "ala ma kota";
		final MutableObject receivedString = new MutableObject(null);
		final Semaphore receivedDataSemaphore = new Semaphore(1);
		receivedDataSemaphore.drainPermits();	//no go with test until data is received

		final ClientConnectionFactory connectionFactory = new ClientConnectionFactory(new FakeTimeProvider(), mock(ConnectionsRegistry.class));
		connectionPool = new ConnectionPool(SelectionKey.OP_READ, new ConnectionPoolConfiguration("reading server"), connectionFactory, handler -> {
			try (Reader userDataReader = new SocketChannelDataReader(handler.channel())) {
				receivedString.setValue(IOUtils.toString(userDataReader));
				receivedDataSemaphore.release();	//synchronize data receiving between threads
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});

		try (ConnectionsAcceptingServer connectionsAcceptingServer = new ConnectionsAcceptingServer(connectionPool)) {
			StoppableThread stoppableThread = new StoppableThread(connectionPool);
			stoppableThread.start();

			try {
				connectionsAcceptingServer.start();

				writeDataToServer(
						connectionsAcceptingServer.getHost(),
						connectionsAcceptingServer.getPort(),
						anyString);

				receivedDataSemaphore.tryAcquire(1_000L, TimeUnit.MILLISECONDS);

				assertThat((String) receivedString.getValue())
						.isEqualTo(anyString);
			} finally {
				stoppableThread.shutdown();
			}
		}
	}

	@Test
	public void clientHandlerExceptionOnProcessClientShouldNotDestroyThread() throws Exception {
		connectionPool = doNothingConnectionPool(failingClientHandler());

		catchException(connectionPool).registerClient(mock(SocketChannel.class));

		assertThat((Exception)caughtException()).isNull();
	}

	@Test
	public void clientHandlerExceptionOnNewClientShouldNotDestroyThread() throws Exception {
		ClientHandler failingClient = new ClientHandler() {
			@Override
			public void onNewClientConnection(ClientConnection newClient) {
				throw new RuntimeException();
			}

			@Override
			public void processClient(ClientConnection clientConnection) {
				//nothing
			}
		};

		connectionPool = new ConnectionPool(
				SelectionKey.OP_WRITE,
				new ConnectionPoolConfiguration("anyName"),
				clientConnectionFactory, failingClient
		);

		try (ConnectionsAcceptingServer connectionsAcceptingServer = new ConnectionsAcceptingServer(connectionPool)) {
			connectionPool.run();
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
			public void processClient(ClientConnection clientConnection) {
				throw new RuntimeException();
			}

			@Override
			public void onNewClientConnection(ClientConnection newClient) {
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

	private ConnectionPool doNothingConnectionPool(ClientHandler clientHandler) throws IOException {
		return new DoNothingConnectionPool(
				anySelectionOperation,
				new ConnectionPoolConfiguration("accept thread")
						.setNewClientsQueueSize(1)
						.setNewClientRegisterTimeout(1),
				clientHandler,
				clientConnectionFactory
		);
	}

	private static class DoNothingConnectionPool extends ConnectionPool {
		public DoNothingConnectionPool(int selectionOperation, ConnectionPoolConfiguration connectionPoolConfiguration, ClientHandler clientHandler, ClientConnectionFactory connectionFactory) throws IOException {
			super(selectionOperation, connectionPoolConfiguration, connectionFactory, clientHandler);
		}

		@Override
		protected void performNewClientsRegistration(List<SocketChannel> newClients) {
			//do nothing
		}
	}
}
