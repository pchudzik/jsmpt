package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import lombok.RequiredArgsConstructor;

/**
 * Created by pawel on 15.04.14.
 */
@RequiredArgsConstructor
class QuitCommandFactory implements CommandActionFactory {
	private final ServerConfiguration serverConfiguration;

	@Override
	public boolean canExecute(Command command) {
		return command.getCommandString().startsWith("quit");
	}

	@Override
	public CommandAction create(ClientConnection clientConnection, Command command) {
		return () -> CommandResponse.commandResponse()
				.response(SmtpResponse.CLOSE)
				.responseMessage(serverConfiguration.getListenAddress() + " Service closing transmission channel")
				.clientAction(clientConnection::close)
				.build();
	}
}
