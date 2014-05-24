package com.pchudzik.jsmtp.server.command.rfc821;

import java.io.StringWriter;

import static org.mockito.Mockito.*;

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
public class QuitCommandTest {
	@Test
	public void shouldCloseClientConnectionWithMessge() throws Exception {
		final String domain = "example.com";
		final ClientConnection clientConnection = mock(ClientConnection.class);
		when(clientConnection.getWriter()).thenReturn(new StringWriter());

		CommandResponse response = new QuitCommandFactory(
						ServerConfiguration.builder()
								.listenAddress(domain)
								.connectionPoolConfiguration(ConnectionPoolConfiguration.defaults)
								.build())
				.create(clientConnection, new Command("quit"))
				.executeCommand();
		response.execute(clientConnection);

		CommandResponseAssert.assertThat(response)
				.hasSmtpResponse(SmtpResponse.CLOSE)
				.hasMessage(domain + " Service closing transmission channel")
				.hasClientAction();
		verify(clientConnection).close();
	}
}
