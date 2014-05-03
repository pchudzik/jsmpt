package com.pchudzik.jsmtp.server.command.rfc821;

import java.io.IOException;
import java.io.Reader;

import static com.pchudzik.jsmtp.server.command.CommandExecutionException.commandExecutionException;
import static com.pchudzik.jsmtp.server.command.CommandResponse.commandResponse;

import com.pchudzik.jsmtp.server.command.*;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Created by pawel on 24.04.14.
 */
public class DataCommandFactory implements CommandActionFactory {
	@Override
	public boolean canExecute(Command command) {
		return command.getCommandString().startsWith("data");
	}

	@Override
	public CommandAction create(ClientConnection clientConnection, Command command) {
		return new DataCommandAction(clientConnection);
	}

	@RequiredArgsConstructor
	static class DataCommandAction implements CommandAction {
		static final String commandEnd = ".";
		private final ClientConnection clientConnection;

		@Override
		public CommandResponse executeCommand() throws CommandExecutionException {
			final MailTransaction mailTx = ClientContextUtilsUtils.getMailTransaction(clientConnection);
			if (mailTx.dataInProgress()) {
				return readMoreDataFromClient(clientConnection.getReader(), mailTx);
			} else {
				clientConnection.getClientContext().put(ContextConstant.pendingCommand, this);
				mailTx.startUserInput();
				return commandResponse()
						.response(SmtpResponse.MAIL_INPUT_START)
						.responseMessage("Start mail input; end with <CRLF>.<CRLF>")
						.commandFinished(false)
						.build();
			}
		}

		private CommandResponse readMoreDataFromClient(Reader clientReader, MailTransaction mailTx) throws CommandExecutionException {
			final boolean isFinished = readDataFromClient(clientReader, mailTx);
			if (isFinished) {
				mailTx.userDataFinished();
				return commandResponse(SmtpResponse.OK);
			}

			return commandResponse()
					.commandFinished(isFinished)
					.build();
		}

		private boolean readDataFromClient(Reader clientReader, MailTransaction mailTx) throws CommandExecutionException {
			try {
				final String[] lastLine = new String[1];
				final StringBuilder buffer = new StringBuilder();
				IOUtils.readLines(clientReader)
						.stream()
						.forEach(line -> {
							lastLine[0] = line;
							if(!commandEnd.equals(line)) {
								buffer.append(line).append("\n");
							}
						});
				mailTx.addUserData(buffer);
				return commandEnd.equals(lastLine[0]);
			} catch (IOException ex) {
				mailTx.reset();
				throw commandExecutionException(SmtpResponse.TRANSACTION_FAILED)
						.cause(ex)
						.responseMessage("Can not reada data from client")
						.build();
			}
		}
	}
}