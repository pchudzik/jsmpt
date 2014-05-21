package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 23.04.14.
 */
public class ResetCommandFactory implements CommandActionFactory {
	@Override
	public boolean canExecute(Command command) {
		return command.getCommandString().startsWith("reset");
	}

	@Override
	public CommandAction create(ClientConnection clientConnection, Command command) {
		return () -> {
			ClientContextUtilsUtils.getMailTransaction(clientConnection).reset();
			return CommandResponse.finishedOkResponse();
		};
	}
}
