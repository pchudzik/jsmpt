package com.pchudzik.jsmtp.server.command.rfc821;

import java.util.Collection;

import static java.util.Arrays.asList;

import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.CommandActionFactory;
import lombok.RequiredArgsConstructor;

/**
 * Created by pawel on 24.05.14.
 */
@RequiredArgsConstructor
public class Rfc821Configuration {
	private final ServerConfiguration serverConfiguration;

	public Collection<? extends CommandActionFactory> getCommands() {
		return asList(
				new DataCommandFactory(),
				new HeloCommandFactory(serverConfiguration),
				new MailFromCommandFactory(),
				new NoopCommandFactory(),
				new QuitCommandFactory(serverConfiguration),
				new RcptToCommandFactory(),
				new ResetCommandFactory());
	}
}
