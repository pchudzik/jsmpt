package com.pchudzik.jsmtp.server.command;

import com.pchudzik.jsmtp.server.nio.pool.ClientConnection;

/**
 * User: pawel
 * Date: 13.04.14
 * Time: 08:42
 */
public interface Command {
	void executeCommand(ClientConnection clientConnection);
}
