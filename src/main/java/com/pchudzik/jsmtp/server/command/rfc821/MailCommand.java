package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.command.SmtpResponse;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;

/**
 * Created by pawel on 16.04.14.
 */
public class MailCommand implements CommandAction, MailConstans {
	@Override
	public void executeCommand(ClientConnection clientConnection, Command command) throws IOException {
		final MailTransaction mailTx = clientConnection.getClientContext().<MailTransaction>getObject(mail).get();
		mailTx.reset();

		try {
			mailTx.setFrom(getFromAddress(command.getCommandString()));
			performCommand(clientConnection, () -> clientConnection.getWriter().write(SmtpResponse.OK + " OK"));
		} catch (AddressException ex) {
			performCommand(clientConnection, () -> clientConnection.getWriter().write(SmtpResponse.MAIL_BOX_NOT_AVAILABLE + " mailbox syntax incorrect"));
		}
	}

	private InternetAddress getFromAddress(String command) throws AddressException {
		String [] cmdWithArguments = command.split(":", 2);
		if(cmdWithArguments.length < 2) {
			throw new AddressException("Missing email address");
		}
		return new InternetAddress(cmdWithArguments[1]);
	}
}
