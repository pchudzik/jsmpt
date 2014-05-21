package com.pchudzik.jsmtp;

import com.pchudzik.jsmtp.common.RandomProvider;
import com.pchudzik.jsmtp.common.StoppableThread;
import com.pchudzik.jsmtp.common.TimeProvider;
import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.SmtpClientHandler;
import com.pchudzik.jsmtp.server.command.rfc821.CommandRegistry;
import com.pchudzik.jsmtp.server.nio.ConnectionsAcceptingServer;
import com.pchudzik.jsmtp.server.nio.pool.ConnectionPoolConfiguration;
import com.pchudzik.jsmtp.server.nio.pool.ConnectionsRegistry;
import com.pchudzik.jsmtp.server.nio.pool.MultiConnectionPool;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnectionFactory;

import java.io.IOException;

/**
 * Created by pawel on 27.04.14.
 */
public class Main {
	public static void main(String[] args) throws IOException {
		final TimeProvider timeProvider = new TimeProvider();
		final RandomProvider randomProvider = new RandomProvider();

		final ServerConfiguration serverConfiguration = new ServerConfiguration();
		serverConfiguration.setListenAddress("localhost");

		final CommandRegistry commandRegistry = new CommandRegistry(serverConfiguration);
		final ConnectionsRegistry connectionsRegistry = new ConnectionsRegistry(timeProvider);
		final MultiConnectionPool connectionPool = new MultiConnectionPool(
				randomProvider,
				new ConnectionPoolConfiguration("cnnection pool").setConnectionPoolsSize(5),
				new ClientConnectionFactory(timeProvider, connectionsRegistry),
				new SmtpClientHandler(commandRegistry));
		final ConnectionsAcceptingServer server = new ConnectionsAcceptingServer("localhost", 9099, connectionPool);
		connectionPool.initialize();
		new StoppableThread(connectionsRegistry, "connection registry").start();
		new StoppableThread(server, "connections accepting thread").start();

		System.out.println("On and running");
	}
}