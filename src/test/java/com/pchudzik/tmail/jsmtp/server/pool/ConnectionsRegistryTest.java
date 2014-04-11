package com.pchudzik.tmail.jsmtp.server.pool;

import com.pchudzik.tmail.jsmtp.server.common.FakeTimeProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.channels.SelectionKey;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by pawel on 11.04.14.
 */
public class ConnectionsRegistryTest {
	private final long now = System.currentTimeMillis();
	private final FakeTimeProvider timeProvider = new FakeTimeProvider(now);

	private final long keepAliveTime = TimeUnit.MINUTES.toMillis(3);
	private final long checkTickTimeout = 500L;
	private final SelectionKey validSelectionKey = validSelectionKeyMock();
	private ConnectionsRegistry registry;

	@BeforeMethod
	public void setupRegistry() {
		registry = newSimpleRegistry(keepAliveTime, checkTickTimeout);
	}

	@Test
	public void shouldRegisterNewClient() {
		registerNewClient(validSelectionKey);

		assertThat(registry.getActiveClientsCount())
				.isEqualTo(1);
	}

	@Test
	public void shouldRemoveBrokenClients() {
		registerNewClient(validSelectionKey);

		registry.clientConnectionError(validSelectionKey);
		simulateNextRegistryTick(0);

		assertThat(registry.getActiveClientsCount()).isEqualTo(0);
	}

	@Test
	public void shouldUpdateClientHeartbeatTime() {
		registerNewClient(validSelectionKey);

		registry.onClientHeartbeat(validSelectionKey);
		simulateNextRegistryTick(keepAliveTime);

		assertThat(registry.getActiveClientsCount()).isEqualTo(1);
	}

	@Test
	public void shouldDisconnectInactiveClients() {
		registerNewClient(validSelectionKey);

		simulateNextRegistryTick(keepAliveTime);

		assertThat(registry.getActiveClientsCount()).isEqualTo(0);
	}

	@Test
	public void shouldDetectClientDisconnection() {
		final SelectionKey disconnectingSelectionKey = mock(SelectionKey.class);
		when(disconnectingSelectionKey.isValid()).thenReturn(true, false);
		registerNewClient(disconnectingSelectionKey);

		//client is connected
		assertThat(registry.getActiveClientsCount()).isEqualTo(1);

		registry.run();

		//client is disconnected
		assertThat(registry.getActiveClientsCount()).isZero();
	}

	private void registerNewClient(SelectionKey validSelectionKey) {
		registry.addNewClient(validSelectionKey);
		registry.run();
	}

	private void simulateNextRegistryTick(long connectionKeepAlive) {
		timeProvider.setNow(now + 2 * connectionKeepAlive);
		registry.run();
	}

	private ConnectionsRegistry newSimpleRegistry(long keepAliveTime, long checkTimeout) {
		return new ConnectionsRegistry(timeProvider, keepAliveTime, checkTimeout);
	}

	private SelectionKey validSelectionKeyMock() {
		final SelectionKey validSelectionKey = mock(SelectionKey.class);
		when(validSelectionKey.isValid()).thenReturn(true);
		return validSelectionKey;
	}
}
