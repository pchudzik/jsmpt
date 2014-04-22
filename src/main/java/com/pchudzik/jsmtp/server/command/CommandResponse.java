package com.pchudzik.jsmtp.server.command;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by pawel on 22.04.14.
 */
public class CommandResponse {
	private final SmtpResponse smtpResponse;
	private final String responseMessage;
	final Optional<ClientAction> clientAction;

	public CommandResponse(SmtpResponse smtpResponse, String responseMessage, ClientAction action) {
		this.smtpResponse = smtpResponse;
		this.responseMessage = responseMessage;
		this.clientAction = Optional.ofNullable(action);
	}
	public CommandResponse(SmtpResponse smtpResponse, String responseMessage) {
		this(smtpResponse, responseMessage, null);
	}

	public CommandResponse(SmtpResponse smtpResponse) {
		this(smtpResponse, null);
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
