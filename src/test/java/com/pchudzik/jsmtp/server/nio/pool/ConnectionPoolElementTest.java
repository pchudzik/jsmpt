package com.pchudzik.jsmtp.server.nio.pool;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.jayway.awaitility.Awaitility.await;
import static com.pchudzik.jsmtp.common.function.FunctionUtils.uncheckedConsumer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.beust.jcommander.internal.Lists;
import com.jayway.awaitility.Duration;
import com.pchudzik.jsmtp.common.FakeTimeProvider;
import com.pchudzik.jsmtp.common.RunnableTask;
import com.pchudzik.jsmtp.common.StoppableThread;
import com.pchudzik.jsmtp.server.ClientHandler;
import com.pchudzik.jsmtp.server.nio.ConnectionsAcceptingServer;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * User: pawel
 * Date: 08.04.14
 * Time: 16:21
 */
@Slf4j
public class ConnectionPoolElementTest {
	private static final Duration THREE_SECONDS = new Duration(3, TimeUnit.SECONDS);
	private final LinkedList<StoppableThread> createdThreads = Lists.newLinkedList();

	private ClientConnectionFactory clientConnectionFactory;
	private ClientConnectionFactory connectionFactory;

	@BeforeMethod
	public void setup() {
		clientConnectionFactory = mock(ClientConnectionFactory.class);
		connectionFactory = new ClientConnectionFactory(
				new FakeTimeProvider(),
				mock(ConnectionsRegistry.class));
	}

	@AfterMethod
	public void shutdownAnyRunningThreads() {
		while (createdThreads.isEmpty()) {
			final StoppableThread thread = createdThreads.pop();
			thread.shutdown();
			await()
					.atMost(THREE_SECONDS)
					.until(() -> thread.isFinished());
		}
	}

	@Test
	public void shouldRejectClientsIfIncomingConnectionsQueueExceeded() throws Exception {
		final ConnectionPool connectionPool = doNothingConnectionPool(doNothingHandler());

		connectionPool.registerClient(mock(SocketChannel.class));

		catchException(connectionPool).registerClient(mock(SocketChannel.class));

		assertThat((Exception)caughtException())
				.isInstanceOf(ClientRejectedException.class);
	}

	@Test
	public void shouldHandleIncomingConnectionFromNewClient() throws Exception {
		final String anyString = "ala ma kota";
		final StringBuilder receivedString = new StringBuilder();

		final ConnectionPoolElement connectionPoolElement = new ConnectionPoolElement(new ConnectionPoolConfiguration("reading server"), connectionFactory, clientConnection -> {
			try (Reader userDataReader = clientConnection.getReader()) {
				receivedString.append(IOUtils.toString(userDataReader));
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});
		final ConnectionsAcceptingServer connectionsAcceptingServer = new ConnectionsAcceptingServer(connectionPoolElement);

		startNewThread(connectionPoolElement);
		startNewThread(connectionsAcceptingServer);

		writeDataToServer(
				connectionsAcceptingServer.getHost(),
				connectionsAcceptingServer.getPort(),
				uncheckedConsumer(outputStream -> {
					outputStream.write(anyString.getBytes());
					outputStream.flush();

					outputStream.write(anyString.getBytes());
					outputStream.flush();
				}));

		await()
				.atMost(THREE_SECONDS)
				.until(() -> assertThat(receivedString.toString()).isEqualTo(anyString + anyString));
	}

	@Test
	public void clientHandlerExceptionOnProcessClientShouldNotDestroyThread() throws Exception {
		final ConnectionPool ConnectionPool = doNothingConnectionPool(failingClientHandler());

		catchException(ConnectionPool).registerClient(mock(SocketChannel.class));

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

		ConnectionPoolElement connectionPoolElement = new ConnectionPoolElement(
				new ConnectionPoolConfiguration("anyName"),
				clientConnectionFactory, failingClient);

		final ConnectionsAcceptingServer connectionsAcceptingServer = new ConnectionsAcceptingServer(connectionPoolElement);

		startNewThread(connectionPoolElement);
		startNewThread(connectionsAcceptingServer);

		writeDataToServer(
				connectionsAcceptingServer.getHost(),
				connectionsAcceptingServer.getPort(),
				uncheckedConsumer(outputStream -> outputStream.write("any content".getBytes())));
		//no exception
	}

	private StoppableThread startNewThread(RunnableTask task) {
		final StoppableThread result = new StoppableThread(task);
		result.start();

		createdThreads.add(result);

		await()
				.atMost(THREE_SECONDS)
				.until(() -> result.isRunning());
		return result;
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

	private void writeDataToServer(String host, int port, Consumer<OutputStream> dataWriter) throws Exception {
		try(Socket clientSocket = new Socket(host, port)) {
			OutputStream os = clientSocket.getOutputStream();
			dataWriter.accept(os);
		}
	}

	private ClientHandler doNothingHandler() {
		return handler -> {};
	}

	private ConnectionPoolElement doNothingConnectionPool(ClientHandler clientHandler) throws IOException {
		return new DoNothingConnectionPoolElement(
				new ConnectionPoolConfiguration("accept thread")
						.setNewClientsQueueSize(1)
						.setNewClientRegisterTimeout(1),
				clientHandler,
				clientConnectionFactory
		);
	}

	private static class DoNothingConnectionPoolElement extends ConnectionPoolElement {
		public DoNothingConnectionPoolElement(ConnectionPoolConfiguration connectionPoolConfiguration, ClientHandler clientHandler, ClientConnectionFactory connectionFactory) throws IOException {
			super(connectionPoolConfiguration, connectionFactory, clientHandler);
		}

		@Override
		protected void performNewClientsRegistration(List<SocketChannel> newClients) {
			//do nothing
		}
	}
}
