package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

import static com.pchudzik.jsmtp.server.command.CommandResponse.commandResponse;

/**
 * Created by pawel on 25.04.14.
 */
public class UnsupportedCommand implements CommandAction {
	@Override
	public boolean canExecute(Command command) {
		return true;
	}

	@Override
	public CommandResponse executeCommand(ClientConnection clientConnection, Command command) throws CommandExecutionException {
		return commandResponse(SmtpResponse.MAIL_BOX_NOT_AVAILABLE);
	}
}
