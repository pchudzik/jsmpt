package com.pchudzik.jsmtp.server.command;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by pawel on 22.04.14.
 */
public class CommandResponse {
	private SmtpResponse smtpResponse;
	private String responseMessage;
	Optional<ClientAction> clientAction = Optional.empty();
	boolean commandFinished = true;

	private CommandResponse() { }

	public static CommandResponseBuilder commandResponse() {
		return new CommandResponseBuilder();
	}

	public static CommandResponse commandResponse(SmtpResponse response) {
		return commandResponse()
				.response(response)
				.build();
	}

	public static class CommandResponseBuilder {
		private final CommandResponse response;

		private CommandResponseBuilder() {
			this.response = new CommandResponse();
		}

		public CommandResponseBuilder response(SmtpResponse smtpResponse) {
			response.smtpResponse = smtpResponse;
			return this;
		}

		public CommandResponseBuilder responseMessage(String message) {
			response.responseMessage = message;
			return this;
		}

		public CommandResponseBuilder clientAction(ClientAction action) {
			response.clientAction = Optional.ofNullable(action);
			return this;
		}

		public CommandResponseBuilder commandFinished(boolean finished) {
			response.commandFinished = finished;
			return this;
		}

		public CommandResponse build() {
			return response;
		}
	}

	public SmtpResponse getSmtpResponse() {
		return smtpResponse;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void executeClientAction() throws IOException {
		if(clientAction.isPresent()) {
			clientAction.get().performAction();
		}
	}

	@FunctionalInterface
	public interface ClientAction {
		void performAction() throws IOException;
	}
}
