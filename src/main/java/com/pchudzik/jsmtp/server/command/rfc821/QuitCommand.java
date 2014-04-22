package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 15.04.14.
 */
class QuitCommand implements CommandAction {
	private final ServerConfiguration serverConfiguration;

	public QuitCommand(ServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

	@Override
	public CommandResponse executeCommand(ClientConnection clientConnection, Command command) throws CommandExecutionException {
		return new CommandResponse(
				SmtpResponse.CLOSE,
				serverConfiguration.getListenAddress() + " Service closing transmission channel",
				clientConnection::close);
	}
}
