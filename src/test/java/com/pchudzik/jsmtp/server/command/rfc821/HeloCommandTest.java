package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import org.testng.annotations.Test;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.mockito.Mockito.mock;

/**
 * Created by pawel on 15.04.14.
 */
public class HeloCommandTest {
	private final String domainName = "example.com";
	private final ClientConnection clientConnection = mock(ClientConnection.class);
	private final HeloCommandFactory heloCommandFactory = new HeloCommandFactory(new ServerConfiguration()
			.setListenAddress(domainName));

	@Test
	public void shouldRespondWithHelloMessageOnValidDomain() throws Exception {
		CommandResponse response = heloCommandFactory.create(clientConnection, new Command("helo " + domainName)).executeCommand();

		CommandResponseAssert.assertThat(response)
				.hasSmtpResponse(SmtpResponse.OK)
				.hasMessage(domainName);
	}

	@Test
	public void shouldCloseClientConnectionOnInvalidDomain() throws Exception {
		final String invalidDomain = "invalid-domain.com";

		catchException(heloCommandFactory.create(clientConnection, new Command("helo " + invalidDomain)))
				.executeCommand();

		CommandExecutionExceptionAssert.assertThat(caughtException())
				.isCritical()
				.hasSmtpResponse(SmtpResponse.SERVICE_UNAVAILABLE)
				.hasMessage(invalidDomain);
	}
}
