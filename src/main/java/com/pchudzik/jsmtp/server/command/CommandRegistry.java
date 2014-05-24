package com.pchudzik.jsmtp.server.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


import static com.pchudzik.jsmtp.server.command.common.ContextAware.getPendingCommand;

import com.google.common.collect.Lists;
import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.rfc821.Rfc821Configuration;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * Created by pawel on 24.05.14.
 */
@Slf4j
public class CommandRegistry {
	private final List<CommandActionFactory> availableCommands = Lists.newLinkedList();

	public CommandRegistry(ServerConfiguration serverConfiguration) {
		availableCommands.addAll(new Rfc821Configuration(serverConfiguration).getCommands());

		availableCommands.add(new UnsupportedCommandFactory());
	}

	public Optional<CommandAction> selectCommand(ClientConnection connection) throws IOException {
		Optional<CommandAction> pendingCommand = getPendingCommand(connection);
		if(pendingCommand.isPresent()) {
			return pendingCommand;
		} else {
			try (BufferedReader reader = connection.getReader()) {
				final String clientInputLine = reader.readLine();
				if (StringUtils.isBlank(clientInputLine)) {
					return Optional.empty();
				}

				final Command command = new Command(clientInputLine);
				log.debug("Received command {} from client {}", command, connection.getId());

				return Optional.of(availableCommands.stream()
						.filter(actionFactory -> actionFactory.canExecute(command))
						.findFirst()
						.map(actionFactory -> actionFactory.create(connection, command))
						.get());
			}
		}
	}
}
