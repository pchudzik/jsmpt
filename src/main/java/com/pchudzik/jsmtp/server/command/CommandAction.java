package com.pchudzik.jsmtp.server.command;

import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 14.04.14.
 */
public interface CommandAction {
	CommandResponse executeCommand(ClientConnection clientConnection, Command command) throws CommandExecutionException;
	boolean canExecute(Command command);
}
