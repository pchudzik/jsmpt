package com.pchudzik.jsmtp.server.command.rfc4954;

import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.command.CommandActionFactory;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 04.06.14.
 */
public class DiggestMd5AuthCommandFactory implements CommandActionFactory {
	@Override
	public CommandAction create(ClientConnection clientConnection, Command command) {
		return null;
	}

	@Override
	public boolean canExecute(Command command) {
		return false;
	}
}
