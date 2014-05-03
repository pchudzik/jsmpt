package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import lombok.RequiredArgsConstructor;

import static com.pchudzik.jsmtp.server.command.CommandExecutionException.criticalCommandExecutionException;

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
		return () -> {
			final String domain = parseDomain(command);
			if(domain.equals(serverConfiguration.getListenAddress())) {
				return CommandResponse.commandResponse()
						.responseMessage(domain)
						.response(SmtpResponse.OK)
						.build();
			} else {
				throw criticalCommandExecutionException(SmtpResponse.SERVICE_UNAVAILABLE)
						.responseMessage(domain)
						.build();
			}
		};
	}

	private String parseDomain(Command command) {
		return command.getCommandString().replaceFirst("helo", "").trim();
	}
}
