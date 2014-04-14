package com.pchudzik.jsmtp.server.command;

import com.pchudzik.jsmtp.server.nio.pool.ClientConnection;

import java.io.IOException;

/**
 * Created by pawel on 14.04.14.
 */
public interface CommandAction {
	default void performCommand(ClientConnection connection, ClientConnectionAction action) {
		try {
			action.perform();
		} catch (IOException e) {
			connection.setBroken(e);
		}
	}

	void executeCommand(ClientConnection clientConnection, Command command) throws IOException;

	@FunctionalInterface
	interface ClientConnectionAction {
		void perform() throws IOException;
	}
}
