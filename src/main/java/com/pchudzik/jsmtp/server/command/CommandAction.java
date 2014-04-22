package com.pchudzik.jsmtp.server.command;

import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

import java.io.IOException;

/**
 * Created by pawel on 14.04.14.
 */
public interface CommandAction {
	void executeCommand(ClientConnection clientConnection, Command command) throws CommandExecutionException, IOException;
}
