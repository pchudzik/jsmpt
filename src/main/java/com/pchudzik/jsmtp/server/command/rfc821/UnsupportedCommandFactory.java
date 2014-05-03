package com.pchudzik.jsmtp.server.command.rfc821;

import static com.pchudzik.jsmtp.server.command.CommandResponse.commandResponse;

import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.command.CommandActionFactory;
import com.pchudzik.jsmtp.server.command.SmtpResponse;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 25.04.14.
 */
public class UnsupportedCommandFactory implements CommandActionFactory {
	@Override
	public boolean canExecute(Command command) {
		return true;
	}

	@Override
	public CommandAction create(ClientConnection clientConnection, Command command) {
		return () -> commandResponse(SmtpResponse.MAIL_BOX_NOT_AVAILABLE);
	}
}
