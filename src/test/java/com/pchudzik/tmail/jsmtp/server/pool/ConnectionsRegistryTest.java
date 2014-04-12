package com.pchudzik.tmail.jsmtp.server.pool;

import com.pchudzik.tmail.jsmtp.server.common.FakeTimeProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by pawel on 11.04.14.
 */
public class ConnectionsRegistryTest {
	private final long now = System.currentTimeMillis();
	private final FakeTimeProvider timeProvider = new FakeTimeProvider(now);

	private final long keepAliveTime = TimeUnit.MINUTES.toMillis(3);
	private final long checkTickTimeout = 500L;
	private ConnectionsRegistry registry;

	@BeforeMethod
	public void setupRegistry() {
		registry = new ConnectionsRegistry(timeProvider, keepAliveTime, checkTickTimeout);
	}

	@Test
	public void shouldRegisterNewClient() throws Exception {
		registerNewClient(validConnectionMock());

		assertThat(registry.getActiveClientsCount())
				.isEqualTo(1);
	}

	@Test
	public void shouldRemoveBrokenClients() throws Exception {
		final ClientConnection connection = connectionMock();
		when(connection.isValid()).thenReturn(false);

		registerNewClient(connection);

		assertThat(registry.getActiveClientsCount()).isEqualTo(0);
		verify(connection, times(1)).close();
	}

	@Test
	public void shouldDisconnectInactiveClients() throws Exception {
		final ClientConnection validClientConnection = validConnectionMock();
		when(validClientConnection.getLastHeartbeat()).thenReturn(now - 2 * keepAliveTime);

		registerNewClient(validClientConnection);


		assertThat(registry.getActiveClientsCount()).isEqualTo(0);
		verify(validClientConnection, times(1)).timeout();
	}

	@Test
	public void keepAliveValidClientConnection() throws Exception {
		final long nowInFuture = now + keepAliveTime;
		final ClientConnection connection = validConnectionMock();

		registerNewClient(connection);

		timeProvider.setNow(nowInFuture);
		when(connection.getLastHeartbeat()).thenReturn(nowInFuture);

		registry.run();

		assertThat(registry.getActiveClientsCount()).isEqualTo(1);
	}

	private void registerNewClient(ClientConnection clientConnection) throws Exception {
		registry.addNewClient(clientConnection);
		registry.run();
	}

	private ClientConnection connectionMock() {
		return mock(ClientConnection.class);
	}

	private ClientConnection validConnectionMock() {
		final ClientConnection validConnection = connectionMock();
		when(validConnection.isValid()).thenReturn(true);
		when(validConnection.getLastHeartbeat()).thenReturn(now);
		return validConnection;
	}
}
