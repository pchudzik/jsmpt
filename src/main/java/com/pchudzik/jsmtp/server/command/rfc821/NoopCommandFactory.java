package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 15.04.14.
 */
class NoopCommandFactory implements CommandActionFactory {
	@Override
	public boolean canExecute(Command command) {
		return command.getCommandString().startsWith("noop");
	}

	@Override
	public CommandAction create(ClientConnection clientConnection, Command command) {
		return () -> CommandResponse.finishedOkResponse();
	}
}
