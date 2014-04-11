package com.pchudzik.tmail.jsmtp.server.pool;

import com.pchudzik.tmail.jsmtp.server.common.FakeTimeProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.nio.channels.SelectionKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by pawel on 11.04.14.
 */
public class ConnectionsRegistryTest {
	private final FakeTimeProvider timeProvider = new FakeTimeProvider();
	ConnectionsRegistry registry;

	@AfterMethod
	public void shutdownRunningRegistry() {
		if(registry != null) {
			registry.shutdown();
		}
	}

	@Test
	public void shouldRegisterNewClient() {
		registry = new ConnectionsRegistry(timeProvider, 10, 100);
		final SelectionKey validSelectionKey = mock(SelectionKey.class);
		when(validSelectionKey.isValid()).thenReturn(true);

		registry.addNewClient(validSelectionKey);
		registry.updateClientData();

		assertThat(registry.getActiveClientsCount())
				.isEqualTo(1);
	}

	@Test
	public void shouldUpdateClientHeartbeatTime() {

	}

	@Test
	public void shouldRemoveBrokenClients() {

	}

	@Test
	public void shouldDetectClientDisconnection() {

	}

	@Test
	public void shouldDisconnectInactiveClients() {

	}

	@Test
	public void shouldWorkAsBackgroundTask() {

	}
}
