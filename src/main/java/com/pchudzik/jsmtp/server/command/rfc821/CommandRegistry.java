package com.pchudzik.jsmtp.server.command.rfc821;

import com.google.common.collect.Lists;
import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.command.CommandActionFactory;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.pchudzik.jsmtp.server.command.rfc821.ClientContextUtilsUtils.getPendingCommand;

/**
 * Created by pawel on 25.04.14.
 */
@Slf4j
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

	public Optional<CommandAction> selectCommand(ClientConnection connection) throws IOException{
		Optional<CommandAction> pendingCommand = getPendingCommand(connection);
		if(pendingCommand.isPresent()) {
			return pendingCommand;
		} else {
			try (BufferedReader reader = connection.getReader()) {
				final String clientInputLine = reader.readLine();
				if(StringUtils.isBlank(clientInputLine)) {
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
