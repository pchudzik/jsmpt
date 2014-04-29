package com.pchudzik.jsmtp.server.command;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

/**
 * Created by pawel on 21.04.14.
 */
public class CommandExecutionException extends Exception {
	@Getter private final boolean critical;
	@Getter private final SmtpResponse smtpResponse;

	private CommandExecutionException(boolean critical, SmtpResponse smtpResponse, String message, Throwable cause) {
		super(message, cause);
		this.critical = critical;
		this.smtpResponse = smtpResponse;
	}

	public static CommandExecutionExceptionBuilder criticalCommandExecutionException(SmtpResponse smtpResponse) {
		return new CommandExecutionExceptionBuilder(CommandExecutionExceptionBuilder.CRITICAL, smtpResponse);
	}

	public static CommandExecutionExceptionBuilder commandExecutionException(SmtpResponse smtpResponse) {
		return new CommandExecutionExceptionBuilder(!CommandExecutionExceptionBuilder.CRITICAL, smtpResponse);
	}

	public static class CommandExecutionExceptionBuilder {
		private static final boolean CRITICAL = true;
		private final boolean critical;
		private final SmtpResponse smtpResponse;
		private String responseMessage;
		private Throwable cause;

		private CommandExecutionExceptionBuilder(boolean critical, SmtpResponse smtpResponse) {
			this.critical = critical;
			this.smtpResponse = smtpResponse;
		}

		public CommandExecutionExceptionBuilder responseMessage(String message) {
			this.responseMessage = message;
			return this;
		}

		public CommandExecutionExceptionBuilder cause(Throwable cause) {
			this.cause = cause;
			return this;
		}

		public CommandExecutionException build() {
			Preconditions.checkNotNull(smtpResponse, "smt response code can not be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(responseMessage));

			return new CommandExecutionException(critical, smtpResponse, responseMessage, cause);
		}
	}
}