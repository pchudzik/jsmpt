package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.mail.MailTransaction;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientContext;

import java.io.StringWriter;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by pawel on 15.04.14.
 */
public class CommandUtils {
	public static StringWriter newWriterForClient(ClientConnection clientConnection) {
		final StringWriter writer = new StringWriter();
		when(clientConnection.getWriter()).thenReturn(writer);
		return writer;
	}

	public static MailTransaction newTransactionForClient(ClientConnection clientConnection) {
		final MailTransaction mailTransaction = mock(MailTransaction.class);
		final ClientContext context = mock(ClientContext.class);

		doReturn(Optional.of(mailTransaction)).when(context).getObject(MailConstans.mail);
		when(clientConnection.getClientContext()).thenReturn(context);

		return mailTransaction;
	}
}
