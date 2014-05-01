package com.pchudzik.jsmtp.server.nio.pool;

import java.io.IOException;
import java.nio.channels.SelectionKey;

import com.pchudzik.jsmtp.common.StoppableThread;
import com.pchudzik.jsmtp.common.TimeProvider;
import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.SmtpClientHandler;
import com.pchudzik.jsmtp.server.command.rfc821.CommandRegistry;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnectionFactory;

/**
 * Created by pawel on 27.04.14.
 */
public class Main {
	public static void main(String[] args) throws IOException {
		final TimeProvider timeProvider = new TimeProvider();

		final ServerConfiguration serverConfiguration = new ServerConfiguration();
		serverConfiguration.setListenAddress("localhost");

		final CommandRegistry commandRegistry = new CommandRegistry(serverConfiguration);
		final ConnectionsRegistry connectionsRegistry = new ConnectionsRegistry(timeProvider);
		final ConnectionPool connectionPool = new ConnectionPool(
				new ConnectionPoolConfiguration("cnnection pool"),
				new ClientConnectionFactory(timeProvider, connectionsRegistry),
				new SmtpClientHandler(commandRegistry));
		final ConnectionsAcceptingServer server = new ConnectionsAcceptingServer("localhost", 9099, connectionPool);
		new StoppableThread(connectionsRegistry, "connection registry").start();
		new StoppableThread(connectionPool, "connection pool").start();
		new StoppableThread(server, "connections accepting thread").start();

		System.out.println("On and running");
	}
}
