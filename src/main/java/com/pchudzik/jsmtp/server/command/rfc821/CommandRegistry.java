package com.pchudzik.jsmtp.server.command.rfc821;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.pchudzik.jsmtp.server.command.rfc821.ClientContextUtilsUtils.getPendingCommand;

import com.google.common.collect.Lists;
import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.command.CommandActionFactory;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 25.04.14.
 */
public class CommandRegistry {
	private List<CommandActionFactory> availableCommands;

	public CommandRegistry(ServerConfiguration serverConfiguration) {
		availableCommands = Lists.newLinkedList(Arrays.asList(
				new HeloCommandFactory(serverConfiguration),
				new MailFromCommandFactory(),
				new RcptToCommandFactory(),
				new DataCommandFactory(),
				new ResetCommandFactory(),
				new NoopCommandFactory(),
				new UnsupportedCommandFactory()
		));
	}

	public CommandAction selectCommand(ClientConnection connection) throws IOException{
		Optional<CommandAction> pendingCommand = getPendingCommand(connection);
		if(pendingCommand.isPresent()) {
			return pendingCommand.get();
		} else {
			try (BufferedReader reader = connection.getReader()) {
				final String clientInputLine = reader.readLine();
				final Command command = new Command(clientInputLine);
				return availableCommands.stream()
						.filter(actionFactory -> actionFactory.canExecute(command))
						.findFirst()
						.map(actionFactory -> actionFactory.create(connection, command))
						.get();
			}
		}
	}
}
