package com.pchudzik.jsmtp.server.command.rfc821;

import com.pchudzik.jsmtp.server.nio.pool.ClientConnection;

import java.io.StringWriter;

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
}
