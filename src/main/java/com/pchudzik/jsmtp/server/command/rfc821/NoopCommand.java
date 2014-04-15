package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.nio.pool.ClientConnection;

import java.io.IOException;

/**
 * Created by pawel on 15.04.14.
 */
class NoopCommand implements CommandAction {
	@Override
	public void executeCommand(ClientConnection clientConnection, Command command) throws IOException {
		//noop
	}
}
