package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.nio.pool.ClientConnection;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.StringWriter;

import static com.pchudzik.jsmtp.server.command.rfc821.CommandUtils.newWriterForClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by pawel on 15.04.14.
 */
public class HeloCommandTest {
	private final String domainName = "example.com";
	private final ClientConnection clientConnection = mock(ClientConnection.class);
	private final HeloCommand heloCommand = new HeloCommand(new ServerConfiguration()
			.setListenAddress(domainName));

	@Test
	public void shouldRespondWithHelloMessageOnValidDomain() throws IOException {
		final StringWriter writer = newWriterForClient(clientConnection);

		heloCommand.executeCommand(clientConnection, new Command("helo " + domainName));

		assertThat(writer.getBuffer().toString())
				.isEqualTo("250 " + domainName);
	}

	@Test
	public void shouldCloseClientConnectionOnInvalidDomain() throws IOException {
		final String invalidDomain = "invalid-domain.com";
		final StringWriter writer = newWriterForClient(clientConnection);

		heloCommand.executeCommand(clientConnection, new Command("helo " + invalidDomain));

		assertThat(writer.getBuffer().toString())
				.isEqualTo("421 " + invalidDomain);
		verify(clientConnection).close();
	}
}
