package com.pchudzik.tmail.smtp.server.nio.pool;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * User: pawel
 * Date: 08.04.14
 * Time: 16:06
 */
@FunctionalInterface
interface ClientHandler {
	default void onNewClient(SocketChannel newClient) {}

	void processClient(SelectionKey selectionKey);
}
