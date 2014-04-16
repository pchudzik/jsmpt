package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.command.SmtpResponse;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

import java.io.IOException;

/**
 * Created by pawel on 15.04.14.
 */
class QuitCommand implements CommandAction {
	private final ServerConfiguration serverConfiguration;

	public QuitCommand(ServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

	@Override
	public void executeCommand(ClientConnection clientConnection, Command command) throws IOException {
		performCommand(clientConnection, () -> {
			clientConnection.getWriter()
					.write(SmtpResponse.CLOSE + " " + serverConfiguration.getListenAddress() + " Service closing transmission channel");
			clientConnection.close();
		});
	}
}
