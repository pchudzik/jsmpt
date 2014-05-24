package com.pchudzik.jsmtp.server.command.rfc821;

import static java.util.Arrays.asList;

import com.google.common.collect.Lists;
import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.command.UnsupportedCommandFactory;
import lombok.RequiredArgsConstructor;

/**
 * Created by pawel on 24.05.14.
 */
@RequiredArgsConstructor
public class Rfc821Configuration {
	private final ServerConfiguration serverConfiguration;

	public Rfc821CommandRegistry commandRegistry() {
		return new Rfc821CommandRegistry(
				Lists.newLinkedList(asList(
						new DataCommandFactory(),
						new HeloCommandFactory(serverConfiguration),
						new MailFromCommandFactory(),
						new NoopCommandFactory(),
						new QuitCommandFactory(serverConfiguration),
						new RcptToCommandFactory(),
						new ResetCommandFactory(),
						new UnsupportedCommandFactory()	//FIXME this command should be initialized in proper way
				))
		);
	}
}
