package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.SmtpResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.mail.internet.InternetAddress;

import static org.mockito.Mockito.verify;

/**
 * Created by pawel on 23.04.14.
 */
public class RcptToCommandTest extends AddressExtractingCommandTest {
	final RcptToCommandFactory rcptToCommandFactory = new RcptToCommandFactory();

	@DataProvider(name = rejectionEmailsDataProvider)
	@Override
	Object[][] rejectionEmailsDataProvider() {
		return new Object[][] {
				{
						rcptToCommandFactory,
						new Command("rcpt to: " + wrongAddress),
						SmtpResponse.MAIL_BOX_NOT_AVAILABLE,
						"Invalid email address"
				}, {
				rcptToCommandFactory,
						new Command("rcpt to:"),
						SmtpResponse.MAIL_BOX_NOT_AVAILABLE,
						"Invalid email address"
				}
		};
	}

	@Test
	public void shouldAddNewRecipient() throws Exception {
		rcptToCommandFactory
				.create(clientConnection, new Command("rcpt to:" + email))
				.executeCommand();

		verify(mailTx).addRecipient(new InternetAddress(email));
	}
}
