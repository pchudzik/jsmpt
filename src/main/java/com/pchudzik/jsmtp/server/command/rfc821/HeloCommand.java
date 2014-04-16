package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.command.SmtpResponse;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

import java.io.IOException;

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
	public void executeCommand(ClientConnection clientConnection, Command command) throws IOException {
		final String domain = parseDomain(command);
		if(domain.equals(serverConfiguration.getListenAddress())) {
			performCommand(clientConnection, () -> {
				clientConnection.getWriter()
						.write(SmtpResponse.OK + " " + domain);
			});
		} else {
			performCommand(clientConnection, () -> {
				clientConnection.getWriter()
						.write(SmtpResponse.SERVICE_UNAVAILABLE + " " + domain);
				clientConnection.close();
			});
		}
	}

	private String parseDomain(Command command) {
		return command.getCommandString().replaceFirst("helo", "").trim();
	}
}
