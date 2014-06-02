package com.pchudzik.jsmtp.server.command.rfc2821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import lombok.RequiredArgsConstructor;

/**
 * Created by pawel on 02.06.14.
 */
@RequiredArgsConstructor
class EhloCommandFactory implements CommandActionFactory {
	private final ServerConfiguration serverConfiguration;

	@Override
	public CommandAction create(ClientConnection clientConnection, Command command) {
		return () -> CommandResponse.commandResponse()
					.responseMessage(serverConfiguration.getListenAddress())
					.response(SmtpResponse.OK)
					.build();
	}

	@Override
	public boolean canExecute(Command command) {
		return command.getCommandString().startsWith("ehlo");
	}
}
