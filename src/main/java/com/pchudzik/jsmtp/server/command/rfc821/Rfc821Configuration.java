package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.api.EmailDeliverer;
import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.CommandActionFactory;
import com.pchudzik.jsmtp.server.command.CommandsProvider;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * Created by pawel on 24.05.14.
 */
@RequiredArgsConstructor
public class Rfc821Configuration implements CommandsProvider {
	private final ServerConfiguration serverConfiguration;
	private final EmailDeliverer emailDeliverer;

	@Override
	public Collection<? extends CommandActionFactory> getCommands() {
		return asList(
				new DataCommandFactory(emailDeliverer),
				new HeloCommandFactory(serverConfiguration),
				new MailFromCommandFactory(),
				new NoopCommandFactory(),
				new QuitCommandFactory(serverConfiguration),
				new RcptToCommandFactory(),
				new ResetCommandFactory());
	}
}
