package com.pchudzik.jsmtp.server.command.rfc2821;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.CommandActionFactory;
import com.pchudzik.jsmtp.server.command.CommandsProvider;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * Created by pawel on 02.06.14.
 */
@RequiredArgsConstructor
public class Rfc2821Configuration implements CommandsProvider {
	private final ServerConfiguration serverConfiguration;

	@Override
	public Collection<? extends CommandActionFactory> getCommands() {
		return asList(new EhloCommandFactory(serverConfiguration));
	}
}
