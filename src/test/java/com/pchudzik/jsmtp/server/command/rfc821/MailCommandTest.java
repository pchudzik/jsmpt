package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.SmtpResponse;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.ClientRejectedException;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.mail.internet.InternetAddress;
import java.io.StringWriter;

import static com.pchudzik.jsmtp.server.command.rfc821.CommandUtils.newTransactionForClient;
import static com.pchudzik.jsmtp.server.command.rfc821.CommandUtils.newWriterForClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * User: pawel
 * Date: 18.04.14
 * Time: 21:37
 */
public class MailCommandTest {
	final String email = "<somebody@example.com>";
	final MailCommand mailCommand = new MailCommand();

	private ClientConnection clientConnection;
	private StringWriter writer;
	private MailTransaction mailTx;

	@BeforeMethod
	public void setupClient() throws ClientRejectedException {
		clientConnection = mock(ClientConnection.class);
		mailTx = newTransactionForClient(clientConnection);
		writer = newWriterForClient(clientConnection);
	}

	@Test
	public void shouldResetMailTransaction() throws Exception {
		mailCommand.executeCommand(clientConnection, new Command("mail from: " + email));

		verify(mailTx).reset();
	}

	@Test
	public void shouldRejectCommandOnInvalidFromAddress() throws Exception {
		mailCommand.executeCommand(clientConnection, new Command("mail from: <wrong address>"));

		assertThat(writer.getBuffer().toString())
				.startsWith(SmtpResponse.MAIL_BOX_NOT_AVAILABLE.toString());
	}

	@Test
	public void shouldRejectCommandOnMissingFromAddress() throws Exception {
		mailCommand.executeCommand(clientConnection, new Command("mail from:"));

		assertThat(writer.getBuffer().toString())
				.startsWith(SmtpResponse.MAIL_BOX_NOT_AVAILABLE.toString());
	}

	@Test
	public void shouldSetEmailAddressInTransaction() throws Exception {
		mailCommand.executeCommand(clientConnection, new Command("mail from: " + email));

		verify(mailTx).setFrom(new InternetAddress(email));
		assertThat(writer.getBuffer().toString())
				.startsWith(SmtpResponse.OK.toString());
	}
}
