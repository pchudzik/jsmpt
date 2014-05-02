package com.pchudzik.jsmtp.server.nio.pool;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.jayway.awaitility.Duration;
import com.pchudzik.jsmtp.common.FakeTimeProvider;
import com.pchudzik.jsmtp.common.StoppableThread;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.mutable.MutableObject;
import org.testng.annotations.Test;

/**
 * User: pawel
 * Date: 08.04.14
 * Time: 16:21
 */
@Slf4j
public class ConnectionPoolTest {
	private static final Duration THREE_SECONDS = new Duration(3, TimeUnit.SECONDS);

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

		final ClientConnectionFactory connectionFactory = new ClientConnectionFactory(new FakeTimeProvider(), mock(ConnectionsRegistry.class));
		connectionPool = new ConnectionPool(new ConnectionPoolConfiguration("reading server"), connectionFactory, clientConnection -> {
			try (Reader userDataReader = clientConnection.getReader()) {
				receivedString.setValue(IOUtils.toString(userDataReader));
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});

		final ConnectionsAcceptingServer connectionsAcceptingServer = new ConnectionsAcceptingServer(connectionPool);
		final StoppableThread clientPoolThread = new StoppableThread(connectionPool);

		try(StoppableThread serverThread = new StoppableThread(connectionsAcceptingServer)) {
			clientPoolThread.start();
			serverThread.start();

			await()
					.atMost(THREE_SECONDS)
					.until(() -> clientPoolThread.isRunning() && serverThread.isRunning());

			writeDataToServer(
					connectionsAcceptingServer.getHost(),
					connectionsAcceptingServer.getPort(),
					anyString);

			await()
					.atMost(THREE_SECONDS)
					.until(() -> assertThat((String) receivedString.getValue()).isEqualTo(anyString));
		} finally {
			clientPoolThread.shutdown();
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
		final ClientHandler failingClient = new ClientHandler() {
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
				new ConnectionPoolConfiguration("anyName"),
				clientConnectionFactory, failingClient
		);

		final ConnectionsAcceptingServer connectionsAcceptingServer = new ConnectionsAcceptingServer(connectionPool);
		final StoppableThread stoppableServer = new StoppableThread(connectionsAcceptingServer);
		final StoppableThread stoppableConnectionPool = new StoppableThread(connectionPool);
		try {
			stoppableConnectionPool.start();
			stoppableServer.start();

			await()
					.atMost(THREE_SECONDS)
					.until(() -> stoppableServer.isRunning() && stoppableConnectionPool.isRunning());

			writeDataToServer(
					connectionsAcceptingServer.getHost(),
					connectionsAcceptingServer.getPort(),
					"any content");
		} finally {
			stoppableConnectionPool.shutdown();
			stoppableServer.shutdown();
		}
		//no exception
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
				new ConnectionPoolConfiguration("accept thread")
						.setNewClientsQueueSize(1)
						.setNewClientRegisterTimeout(1),
				clientHandler,
				clientConnectionFactory
		);
	}

	private static class DoNothingConnectionPool extends ConnectionPool {
		public DoNothingConnectionPool(ConnectionPoolConfiguration connectionPoolConfiguration, ClientHandler clientHandler, ClientConnectionFactory connectionFactory) throws IOException {
			super(connectionPoolConfiguration, connectionFactory, clientHandler);
		}

		@Override
		protected void performNewClientsRegistration(List<SocketChannel> newClients) {
			//do nothing
		}
	}
}
