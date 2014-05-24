package com.pchudzik.jsmtp.server.command.rfc821;

import javax.mail.internet.AddressException;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.command.common.ContextAware;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 23.04.14.
 */
class RcptToCommandFactory implements CommandActionFactory, ContextAware {
	@Override
	public boolean canExecute(Command command) {
		return command.getCommandString().startsWith("rcpt");
	}

	@Override
	public CommandAction create(ClientConnection clientConnection, Command command) {
		return () -> {
			MailTransaction mailTx = getMailTransaction(clientConnection);

			try {
				mailTx.addRecipient(AddressExtractor.getAddress(command));
			} catch (AddressException ex) {
				throw CommandExecutionException.commandExecutionException(SmtpResponse.MAIL_BOX_NOT_AVAILABLE)
						.responseMessage("Invalid email address")
						.cause(ex)
						.build();
			}
			return CommandResponse.commandResponse()
					.response(SmtpResponse.OK)
					.build();
		};
	}
}
