package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static com.pchudzik.jsmtp.server.command.CommandExecutionException.commandExecutionException;

/**
 * Created by pawel on 16.04.14.
 */
public class MailFromCommand implements CommandAction, MailConstans {
	@Override
	public CommandResponse executeCommand(ClientConnection clientConnection, Command command) throws CommandExecutionException {
		final MailTransaction mailTx = clientConnection.getClientContext().<MailTransaction>getObject(mail).get();
		mailTx.reset();

		try {
			mailTx.setFrom(getFromAddress(command.getCommandString()));
			return new CommandResponse(SmtpResponse.OK, "OK");
		} catch (AddressException ex) {
			throw commandExecutionException(SmtpResponse.MAIL_BOX_NOT_AVAILABLE)
					.responseMessage("Invalid email address")
					.cause(ex)
					.build();
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
