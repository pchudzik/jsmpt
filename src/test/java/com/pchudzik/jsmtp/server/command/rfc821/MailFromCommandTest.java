package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandResponse;
import com.pchudzik.jsmtp.server.command.CommandResponseAssert;
import com.pchudzik.jsmtp.server.command.SmtpResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.mail.internet.InternetAddress;

import static org.mockito.Mockito.verify;

/**
 * User: pawel
 * Date: 18.04.14
 * Time: 21:37
 */
public class MailFromCommandTest extends AddressExtractingCommandTest {
	final MailFromCommandFactory mailFromCommandFactory = new MailFromCommandFactory();

	@DataProvider(name = rejectionEmailsDataProvider)
	@Override
	Object[][] rejectionEmailsDataProvider() {
		return new Object[][] {
				{
						mailFromCommandFactory,
						new Command("mail from: <wrong address>"),
						SmtpResponse.MAIL_BOX_NOT_AVAILABLE,
						"Invalid email address"
				}, {
						mailFromCommandFactory,
						new Command("mail from:"),
						SmtpResponse.MAIL_BOX_NOT_AVAILABLE,
						"Invalid email address"
				}
		};
	}

	@Test
	public void shouldResetMailTransaction() throws Exception {
		mailFromCommandFactory.create(clientConnection, new Command("mail from: " + email)).executeCommand();

		verify(mailTx).reset();
	}

	@Test
	public void shouldSetEmailAddressInTransaction() throws Exception {
		CommandResponse response = mailFromCommandFactory.create(clientConnection, new Command("mail from: " + email)).executeCommand();

		verify(mailTx).setFrom(new InternetAddress(email));
		CommandResponseAssert.assertThat(response)
				.hasSmtpResponse(SmtpResponse.OK);
	}
}
