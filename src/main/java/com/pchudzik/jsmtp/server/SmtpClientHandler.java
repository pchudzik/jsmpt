package com.pchudzik.jsmtp.server;

import java.io.IOException;
import java.util.Optional;

import static com.pchudzik.jsmtp.server.command.CommandResponse.commandResponse;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.command.CommandResponse.ClientAction;
import com.pchudzik.jsmtp.server.command.common.ContextConstant;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by pawel on 27.04.14.
 */
@Slf4j
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
			final Optional<CommandAction> commandAction = commandRegistry.selectCommand(clientConnection);
			if(commandAction.isPresent()) {
				commandResponse = commandAction.get().executeCommand();
			}
		} catch (CommandExecutionException e) {
			commandResponse = commandResponse()
					.response(e.getSmtpResponse())
					.clientAction(e.isCritical() ? clientConnection::close : ClientAction.noAction)
					.responseMessage(e.getMessage())
					.build();
		}

		if(commandResponse != null) {
			commandResponse.execute(clientConnection);
			log.debug("response for {} finished", clientConnection.getId());
		}
	}
}
