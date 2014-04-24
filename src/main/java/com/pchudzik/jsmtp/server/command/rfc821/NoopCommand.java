package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 15.04.14.
 */
class NoopCommand implements CommandAction {
	@Override
	public CommandResponse executeCommand(ClientConnection clientConnection, Command command) throws CommandExecutionException {
		return CommandResponse.commandResponse(SmtpResponse.OK);
	}
}
