package com.pchudzik.jsmtp.server.command;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

/**
 * Created by pawel on 22.04.14.
 */
public class CommandResponseAssert extends AbstractAssert<CommandResponseAssert, CommandResponse> {
	protected CommandResponseAssert(CommandResponse actual) {
		super(actual, CommandResponseAssert.class);
	}

	public static CommandResponseAssert assertThat(CommandResponse actual) {
		return new CommandResponseAssert(actual);
	}

	public CommandResponseAssert hasSmtpResponse(SmtpResponse smtpResponse) {
		isNotNull();

		SmtpResponseAssert.assertThat(actual.getSmtpResponse())
				.hasSmtpResponse(smtpResponse);

		return this;
	}

	public CommandResponseAssert hasMessage(String expectedMessage) {
		isNotNull();

		if(!StringUtils.equals(actual.getResponseMessage(), expectedMessage)) {
			failWithMessage("Expected message\n<%s>\nbut was\n<%s>", expectedMessage, actual.getResponseMessage());
		}

		return this;
	}

	public CommandResponseAssert hasClientAction() {
		isNotNull();

		Assertions.assertThat(actual.clientAction.isPresent())
				.isTrue();

		return this;
	}
}
