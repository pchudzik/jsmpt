package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

import static com.pchudzik.jsmtp.server.command.CommandExecutionException.criticalCommandExecutionException;

/**
 * User: pawel
 * Date: 13.04.14
 * Time: 08:44
 */
class HeloCommand implements CommandAction {
	private final ServerConfiguration serverConfiguration;

	HeloCommand(ServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

	@Override
	public CommandResponse executeCommand(ClientConnection clientConnection, Command command) throws CommandExecutionException {
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
	}

	private String parseDomain(Command command) {
		return command.getCommandString().replaceFirst("helo", "").trim();
	}
}
