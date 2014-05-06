package com.pchudzik.jsmtp.server;

import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.command.CommandExecutionException;
import com.pchudzik.jsmtp.server.command.CommandResponse;
import com.pchudzik.jsmtp.server.command.CommandResponse.ClientAction;
import com.pchudzik.jsmtp.server.command.SmtpResponse;
import com.pchudzik.jsmtp.server.command.rfc821.CommandRegistry;
import com.pchudzik.jsmtp.server.command.rfc821.ContextConstant;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

import java.io.IOException;

import static com.pchudzik.jsmtp.server.command.CommandResponse.commandResponse;

/**
 * Created by pawel on 27.04.14.
 */
public class SmtpClientHandler implements ClientHandler {
	private final CommandRegistry commandRegistry;

	public SmtpClientHandler(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

	@Override
	public void onNewClientConnection(ClientConnection newClient) throws IOException {
		newClient.getClientContext()
				.put(ContextConstant.mail, new MailTransaction());

		CommandResponse.commandResponse()
				.response(SmtpResponse.HELLO)
				.responseMessage("localhost Simple Mail Transfer Service Ready")
				.build()
				.execute(newClient);
	}

	@Override
	public void processClient(ClientConnection clientConnection) throws IOException {
		CommandResponse commandResponse = null;
		try {
			final CommandAction commandAction = commandRegistry.selectCommand(clientConnection);
			commandResponse = commandAction.executeCommand();
		} catch (CommandExecutionException e) {
			commandResponse = commandResponse()
					.response(e.getSmtpResponse())
					.clientAction(e.isCritical() ? clientConnection::close : ClientAction.noAction)
					.responseMessage(e.getMessage())
					.build();
		}

		if(commandResponse != null) {
			commandResponse.execute(clientConnection);
		}
	}
}
