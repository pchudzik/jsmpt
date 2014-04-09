package com.pchudzik.tmail.jsmtp.server.pool;

import com.pchudzik.tmail.jsmtp.server.ClientRejectedException;
import org.apache.commons.lang.mutable.MutableObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

		serverThread = new ServerThread(SelectionKey.OP_READ, new ServerThreadConfiguration("reading server"), handler -> {
			try {
				StringBuilder clientContent = new StringBuilder();
				SocketChannel socketChannel = (SocketChannel) handler.channel();
				ByteBuffer buffer = ByteBuffer.allocate(128);
				int readCount = 0;
				while((readCount = socketChannel.read(buffer)) > 0) {
					byte [] readBytes = new byte[readCount];
					buffer.rewind();
					buffer.get(readBytes);
					clientContent.append(new String(readBytes));
					buffer.reset();
				}
				receivedString.setValue(clientContent.toString());
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});
		serverThread.start();

		int port = 39393;
		String host = "localhost";
		AtomicBoolean isRunning = new AtomicBoolean(true);
		final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket = serverSocketChannel.socket();
		serverSocket.bind(new InetSocketAddress(host, port));
		Selector serverSelector = Selector.open();
		serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);

		Thread acceptingConnectionsThread = new Thread() {
			@Override
			public void run() {
				while (isRunning.get()) {
					try {
						if(serverSelector.select() > 0) {
							Iterator<SelectionKey> it = serverSelector.selectedKeys().iterator();
							if(it.hasNext()) {
								SelectionKey key = it.next();
								if(key.isAcceptable()) {
									ServerSocketChannel channel = (ServerSocketChannel) key.channel();
									channel.configureBlocking(false);
									serverThread.registerClient(channel.accept());
								}
							}
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		};
		acceptingConnectionsThread.start();

		try(Socket clientSocket = new Socket(host, port)) {
			OutputStream os = clientSocket.getOutputStream();
			os.write(anyString.getBytes());
			os.flush();
		}

		assertThat((String)receivedString.getValue())
				.isEqualTo(anyString);
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
	public void clientHandlerExceptionOnNewClientShouldNotDestroyThread() {

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
