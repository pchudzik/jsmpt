package com.pchudzik.jsmtp.server.nio.pool;

import java.nio.channels.SocketChannel;

/**
 * Created by pawel on 04.05.14.
 */
public interface ConnectionPool {
	void registerClient(SocketChannel newClient) throws ClientRejectedException;
}
