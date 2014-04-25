package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

import javax.mail.internet.AddressException;

import static com.pchudzik.jsmtp.server.command.CommandExecutionException.commandExecutionException;
import static com.pchudzik.jsmtp.server.command.rfc821.ClientContextUtilsUtils.getMailTransaction;

/**
 * Created by pawel on 16.04.14.
 */
public class MailFromCommand implements CommandAction {
	@Override
	public boolean canExecute(Command command) {
		return command.getCommandString().startsWith("mail");
	}

	@Override
	public CommandResponse executeCommand(ClientConnection clientConnection, Command command) throws CommandExecutionException {
		final MailTransaction mailTx = getMailTransaction(clientConnection);
		mailTx.reset();

		try {
			mailTx.setFrom(AddressExtractor.getAddress(command));
			return CommandResponse.commandResponse(SmtpResponse.OK);
		} catch (AddressException ex) {
			throw commandExecutionException(SmtpResponse.MAIL_BOX_NOT_AVAILABLE)
					.responseMessage("Invalid email address")
					.cause(ex)
					.build();
		}
	}
}
