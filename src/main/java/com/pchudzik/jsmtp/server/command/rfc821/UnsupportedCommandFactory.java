package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.command.CommandActionFactory;
import com.pchudzik.jsmtp.server.command.SmtpResponse;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import lombok.extern.slf4j.Slf4j;

import static com.pchudzik.jsmtp.server.command.CommandResponse.commandResponse;

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
