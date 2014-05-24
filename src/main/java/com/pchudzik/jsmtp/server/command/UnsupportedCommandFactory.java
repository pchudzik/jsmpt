package com.pchudzik.jsmtp.server.command;

import static com.pchudzik.jsmtp.server.command.CommandResponse.commandResponse;

import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by pawel on 25.04.14.
 */
@Slf4j
public class UnsupportedCommandFactory implements CommandActionFactory {
	@Override
	public boolean canExecute(Command command) {
		return true;
	}

	@Override
	public CommandAction create(ClientConnection clientConnection, Command command) {
		log.debug("Unsupported command " + command);
		return () -> commandResponse()
				.response(SmtpResponse.MAIL_BOX_NOT_AVAILABLE)
				.build();
	}
}
