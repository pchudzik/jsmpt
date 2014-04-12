package com.pchudzik.tmail.jsmtp.server.nio.pool;

import java.io.IOException;

/**
 * User: pawel
 * Date: 08.04.14
 * Time: 16:06
 */
@FunctionalInterface
interface ClientHandler {
	default void onNewClientConnection(ClientConnection newClient) throws IOException {}

	void processClient(ClientConnection clientConnection) throws IOException;
}
