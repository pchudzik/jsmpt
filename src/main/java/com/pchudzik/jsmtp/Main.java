package com.pchudzik.jsmtp;


import java.io.IOException;

import com.pchudzik.jsmtp.server.Server;
import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.ServerConfiguration.ConnectionPoolConfiguration;

/**
 * Created by pawel on 27.04.14.
 */
public class Main {
	public static void main(String[] args) throws IOException {
		Server server = Server.builder()
				.serverConfiguration(ServerConfiguration.builder()
						.listenAddress("localhost")
						.port(9099)
						.connectionPoolConfiguration(ConnectionPoolConfiguration.defaults)
						.build())
				.withShutdownHook(true)
				.build();
		server.start();
	}
}
