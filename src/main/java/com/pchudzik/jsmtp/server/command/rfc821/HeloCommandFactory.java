package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import lombok.RequiredArgsConstructor;

/**
 * User: pawel
 * Date: 13.04.14
 * Time: 08:44
 */
@RequiredArgsConstructor
class HeloCommandFactory implements CommandActionFactory {
	private final ServerConfiguration serverConfiguration;

	@Override
	public boolean canExecute(Command command) {
		return command.getCommandString().startsWith("helo");
	}

	@Override
	public CommandAction create(ClientConnection clientConnection, Command command) {
		return () -> CommandResponse.commandResponse()
				.responseMessage(serverConfiguration.getListenAddress())
				.response(SmtpResponse.OK)
				.build();
	}
}
