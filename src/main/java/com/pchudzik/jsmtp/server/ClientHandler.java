package com.pchudzik.jsmtp.server;

import java.io.IOException;

import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnection;

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
