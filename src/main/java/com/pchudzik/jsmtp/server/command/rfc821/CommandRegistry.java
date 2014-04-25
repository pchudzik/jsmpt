package com.pchudzik.jsmtp.server.command.rfc821;

import com.google.common.collect.Lists;
import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.Command;
import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

import java.util.Arrays;
import java.util.List;

import static com.pchudzik.jsmtp.server.command.rfc821.ClientContextUtilsUtils.getPendingCommand;

/**
 * Created by pawel on 25.04.14.
 */
public class CommandRegistry {
	private List<CommandAction> availableCommands;

	public CommandRegistry(ServerConfiguration serverConfiguration) {
		availableCommands = Lists.newLinkedList(Arrays.asList(
				new HeloCommand(serverConfiguration),
				new MailFromCommand(),
				new RcptToCommand(),
				new DataCommand(),
				new ResetCommand(),
				new NoopCommand(),
				new UnsupportedCommand()
		));
	}

	public CommandAction selectCommand(ClientConnection connection, Command command) {
		return getPendingCommand(connection)
				.orElseGet(() -> availableCommands
						.stream()
						.filter(action -> action.canExecute(command))
						.findFirst()
						.get());
	}
}
