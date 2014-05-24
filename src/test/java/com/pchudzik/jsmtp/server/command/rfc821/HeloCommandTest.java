package com.pchudzik.jsmtp.server.command.rfc821;

import static org.mockito.Mockito.mock;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.ServerConfiguration.ConnectionPoolConfiguration;
import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandResponse;
import com.pchudzik.jsmtp.server.command.CommandResponseAssert;
import com.pchudzik.jsmtp.server.command.SmtpResponse;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import org.testng.annotations.Test;

/**
 * Created by pawel on 15.04.14.
 */
public class HeloCommandTest {
	private final String serverListenAddress = "example.com";
	private final ClientConnection clientConnection = mock(ClientConnection.class);

	private final HeloCommandFactory heloCommandFactory = new HeloCommandFactory(ServerConfiguration.builder()
			.listenAddress(serverListenAddress)
			.connectionPoolConfiguration(ConnectionPoolConfiguration.defaults)
			.build());

	@Test
	public void shouldRespondWithHelloMessageWithListenAddress() throws Exception {
		CommandResponse response = heloCommandFactory.create(clientConnection, new Command("helo localhost")).executeCommand();

		CommandResponseAssert.assertThat(response)
				.hasSmtpResponse(SmtpResponse.OK)
				.hasMessage(serverListenAddress);
	}
}
