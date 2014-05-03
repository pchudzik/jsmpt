package com.pchudzik.jsmtp.server.command;

import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 03.05.14.
 */
public interface CommandActionFactory {
	CommandAction create(ClientConnection clientConnection, Command command);
	boolean canExecute(Command command);
}
