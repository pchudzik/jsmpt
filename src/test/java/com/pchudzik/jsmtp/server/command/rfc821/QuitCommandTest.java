package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import org.testng.annotations.Test;

import java.io.StringWriter;

import static com.pchudzik.jsmtp.server.command.rfc821.CommandUtils.newWriterForClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by pawel on 15.04.14.
 */
public class QuitCommandTest {
	@Test
	public void shouldCloseClientConnectionWithMessge() throws Exception {
		final String domain = "example.com";
		final ClientConnection clientConnection = mock(ClientConnection.class);
		final StringWriter writer = newWriterForClient(clientConnection);

		new QuitCommand(new ServerConfiguration().setListenAddress(domain))
				.executeCommand(clientConnection, new Command("quit"));

		assertThat(writer.getBuffer().toString())
				.isEqualTo("221 " + domain + " Service closing transmission channel");
		verify(clientConnection).close();
	}
}
