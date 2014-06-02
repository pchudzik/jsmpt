package com.pchudzik.jsmtp.server.command;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.pchudzik.jsmtp.server.command.common.ContextAware;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.pchudzik.jsmtp.common.function.FunctionUtils.uncheckedSupplier;

/**
 * Created by pawel on 24.05.14.
 */
@Slf4j
public class CommandRegistry implements ContextAware {
	@VisibleForTesting
	final List<CommandActionFactory> availableCommands = Lists.newLinkedList();

	public CommandRegistry(Collection<? extends CommandsProvider> commandProviders) {
		commandProviders.forEach(provider -> availableCommands.addAll(provider.getCommands()));
	}

	public Optional<CommandAction> selectCommand(ClientConnection connection) throws IOException {
		return Optional.ofNullable(
				getPendingCommand(connection)
						.orElseGet(uncheckedSupplier(() -> {
							try (BufferedReader reader = connection.getReader()) {
								final String clientInputLine = reader.readLine();
								if (StringUtils.isBlank(clientInputLine)) {
									return null;
								}

								final Command command = new Command(clientInputLine);
								log.debug("Received command {} from client {}", command, connection.getId());

								return availableCommands.stream()
										.filter(actionFactory -> actionFactory.canExecute(command))
										.findFirst()
										.map(actionFactory -> actionFactory.create(connection, command))
										.get();
							}
						})));
	}
}
