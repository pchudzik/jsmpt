package com.pchudzik.jsmtp.server.command.rfc2821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class EhloCommandFactoryTest {
	private final String serverListenAddress = "example.com";
	private final ClientConnection clientConnection = mock(ClientConnection.class);

	private final EhloCommandFactory ehloCommandFactory = new EhloCommandFactory(ServerConfiguration.builder()
			.listenAddress(serverListenAddress)
			.build());

	@Test
	public void shouldRespondOnlyToEhloCommand() {
		assertThat(ehloCommandFactory.canExecute(new Command("EHLO " + serverListenAddress)))
				.isTrue();

		assertThat(ehloCommandFactory.canExecute(new Command("helo " + serverListenAddress)))
				.isFalse();
	}
}