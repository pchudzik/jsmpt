package com.pchudzik.jsmtp.server.nio.pool;

import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

import java.io.IOException;

/**
 * User: pawel
 * Date: 08.04.14
 * Time: 16:06
 */
@FunctionalInterface
public interface ClientHandler {
	default void onNewClientConnection(ClientConnection newClient) throws IOException {}

	void processClient(ClientConnection clientConnection) throws IOException;
}
