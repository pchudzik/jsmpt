package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

/**
 * Created by pawel on 23.04.14.
 */
class MailTransactionUtils implements MailConstans {
	public static MailTransaction getMailTransaction(ClientConnection clientConnection) {
		return clientConnection.getClientContext().<MailTransaction>getObject(MailConstans.mail).get();
	}
}
