package com.pchudzik.tmail.jsmtp.server.pool;

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
