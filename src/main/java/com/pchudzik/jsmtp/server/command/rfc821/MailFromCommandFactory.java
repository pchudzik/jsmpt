package com.pchudzik.jsmtp.server.command.rfc821;

import javax.mail.internet.AddressException;

import static com.pchudzik.jsmtp.server.command.CommandExecutionException.commandExecutionException;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.command.common.ContextAware;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 16.04.14.
 */
public class MailFromCommandFactory implements CommandActionFactory, ContextAware {
	@Override
	public boolean canExecute(Command command) {
		return command.getCommandString().startsWith("mail");
	}

	@Override
	public CommandAction create(ClientConnection clientConnection, Command command) {
		return () -> {
			final MailTransaction mailTx = getMailTransaction(clientConnection);
			mailTx.reset();

			try {
				mailTx.setFrom(AddressExtractor.getAddress(command));
				return CommandResponse.finishedOkResponse();
			} catch (AddressException ex) {
				throw commandExecutionException(SmtpResponse.MAIL_BOX_NOT_AVAILABLE)
						.responseMessage("Invalid email address")
						.cause(ex)
						.build();
			}
		};
	}
}
