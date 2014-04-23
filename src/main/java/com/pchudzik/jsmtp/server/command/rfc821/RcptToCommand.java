package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

import javax.mail.internet.AddressException;

/**
 * Created by pawel on 23.04.14.
 */
public class RcptToCommand implements CommandAction, MailConstans {
	@Override
	public CommandResponse executeCommand(ClientConnection clientConnection, Command command) throws CommandExecutionException {
		MailTransaction mailTx = clientConnection.getClientContext().<MailTransaction>getObject(mail).get();

		try {
			mailTx.addRecipient(AddressExtractor.getAddress(command));
		} catch (AddressException ex) {
			throw CommandExecutionException.commandExecutionException(SmtpResponse.MAIL_BOX_NOT_AVAILABLE)
					.responseMessage("Invalid email address")
					.cause(ex)
					.build();
		}
		return new CommandResponse(SmtpResponse.OK);
	}
}
