package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 23.04.14.
 */
public class ResetCommand implements CommandAction {
	@Override
	public CommandResponse executeCommand(ClientConnection clientConnection, Command command) throws CommandExecutionException {
		MailTransactionUtils.getMailTransaction(clientConnection).reset();

		return CommandResponse.commandResponse(SmtpResponse.OK);
	}
}
