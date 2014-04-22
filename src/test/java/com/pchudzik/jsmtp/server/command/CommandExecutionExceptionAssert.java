package com.pchudzik.jsmtp.server.command;

import org.assertj.core.api.AbstractThrowableAssert;

/**
 * Created by pawel on 22.04.14.
 */
public class CommandExecutionExceptionAssert extends AbstractThrowableAssert<CommandExecutionExceptionAssert, CommandExecutionException> {
	protected CommandExecutionExceptionAssert(CommandExecutionException actual) {
		super(actual, CommandExecutionExceptionAssert.class);
	}

	public static CommandExecutionExceptionAssert assertThat(CommandExecutionException actual) {
		return new CommandExecutionExceptionAssert(actual);
	}

	public CommandExecutionExceptionAssert isCritical() {
		return isCritical(true);
	}

	public CommandExecutionExceptionAssert isNotCritical() {
		return isCritical(false);
	}

	public CommandExecutionExceptionAssert hasSmtpResponse(SmtpResponse smtpResponse) {
		isNotNull();

		SmtpResponseAssert.assertThat(actual.getSmtpResponse())
				.hasSmtpResponse(smtpResponse);

		return this;
	}


	private CommandExecutionExceptionAssert isCritical(boolean isCritical) {
		isNotNull();

		if(actual.isCritical() != isCritical) {
			failWithMessage("Expected exception to <%s> be critical but was <%s>",
					isCritical ? "" : "not",
					actual.isCritical() ? "critical" : "not critical");
		}

		return this;
	}
}
