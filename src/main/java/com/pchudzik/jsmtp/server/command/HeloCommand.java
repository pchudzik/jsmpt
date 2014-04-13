package com.pchudzik.jsmtp.server.command;

import com.pchudzik.jsmtp.server.nio.pool.ClientConnection;

/**
 * User: pawel
 * Date: 13.04.14
 * Time: 08:44
 */
class HeloCommand implements Command {
	@Override
	public void executeCommand(ClientConnection clientConnection) {
		clientConnection.write("hello");
	}
}
