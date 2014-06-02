package com.pchudzik.jsmtp.server.command;

import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * Created by pawel on 02.06.14.
 */
public class FallbackCommandsProvider implements CommandsProvider {
	@Override
	public Collection<? extends CommandActionFactory> getCommands() {
		return asList(new UnsupportedCommandFactory());
	}
}
