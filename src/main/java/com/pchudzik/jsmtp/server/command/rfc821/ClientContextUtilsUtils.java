package com.pchudzik.jsmtp.server.command.rfc821;

import java.util.Optional;

import com.pchudzik.jsmtp.server.command.CommandAction;
import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 23.04.14.
 */
class ClientContextUtilsUtils implements ContextConstant {
	public static MailTransaction getMailTransaction(ClientConnection clientConnection) {
		return clientConnection.getClientContext().<MailTransaction>getObject(ContextConstant.mail).get();
	}

	public static Optional<CommandAction> getPendingCommand(ClientConnection connection) {
		return connection.getClientContext().getObject(ContextConstant.pendingCommand);
	}
}
