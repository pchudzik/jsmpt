package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.command.CommandActionFactory;
import com.pchudzik.jsmtp.server.command.CommandResponse;
import com.pchudzik.jsmtp.server.command.common.ContextAware;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 23.04.14.
 */
public class ResetCommandFactory implements CommandActionFactory, ContextAware {
	@Override
	public boolean canExecute(Command command) {
		return command.getCommandString().startsWith("reset");
	}

	@Override
	public CommandAction create(ClientConnection clientConnection, Command command) {
		return () -> {
			getMailTransaction(clientConnection).reset();
			return CommandResponse.finishedOkResponse();
		};
	}
}
