package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.ClientRejectedException;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.mail.internet.InternetAddress;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.pchudzik.jsmtp.server.command.rfc821.CommandUtils.newTransactionForClient;
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
	private MailTransaction mailTx;

	@BeforeMethod
	public void setupClient() throws ClientRejectedException {
		clientConnection = mock(ClientConnection.class);
		mailTx = newTransactionForClient(clientConnection);
	}

	@Test
	public void shouldResetMailTransaction() throws Exception {
		mailCommand.executeCommand(clientConnection, new Command("mail from: " + email));

		verify(mailTx).reset();
	}

	@Test
	public void shouldRejectCommandOnInvalidFromAddress() throws Exception {
		catchException(mailCommand).executeCommand(clientConnection, new Command("mail from: <wrong address>"));

		CommandExecutionExceptionAssert.assertThat(caughtException())
				.isNotCritical()
				.hasSmtpResponse(SmtpResponse.MAIL_BOX_NOT_AVAILABLE)
				.hasMessage("Invalid email address");
	}

	@Test
	public void shouldRejectCommandOnMissingFromAddress() throws Exception {
		catchException(mailCommand).executeCommand(clientConnection, new Command("mail from:"));

		CommandExecutionExceptionAssert.assertThat(caughtException())
				.isNotCritical()
				.hasSmtpResponse(SmtpResponse.MAIL_BOX_NOT_AVAILABLE)
				.hasMessage("Invalid email address");
	}

	@Test
	public void shouldSetEmailAddressInTransaction() throws Exception {
		CommandResponse response = mailCommand.executeCommand(clientConnection, new Command("mail from: " + email));

		verify(mailTx).setFrom(new InternetAddress(email));
		CommandResponseAssert.assertThat(response)
				.hasSmtpResponse(SmtpResponse.OK);
	}
}
