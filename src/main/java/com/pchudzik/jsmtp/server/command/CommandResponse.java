package com.pchudzik.jsmtp.server.command;

import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.security.cert.CRL;
import java.util.Optional;

/**
 * Created by pawel on 22.04.14.
 */
@Slf4j
public class CommandResponse {
	private static final String CRLF = "\r\n";

	protected SmtpResponse smtpResponse;
	protected String responseMessage;
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

	public void execute(ClientConnection clientConnection) throws IOException {
		if(commandFinished) {
			StringBuilder response = new StringBuilder()
					.append(smtpResponse.getCode() + " ")
					.append(StringUtils.isNotBlank(responseMessage) ? responseMessage : smtpResponse.toString())
					.append(CRLF);
			clientConnection.getWriter()
					.append(response.toString())
					.flush();

			log.debug("send command {}", response);
		}
		clientAction.orElse(ClientAction.noAction).performAction();
	}

	@FunctionalInterface
	public interface ClientAction {
		ClientAction noAction = () -> {};
		void performAction() throws IOException;
	}
}
