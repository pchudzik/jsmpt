package com.pchudzik.jsmtp.server;

import com.pchudzik.jsmtp.api.EmailDeliverer;
import com.pchudzik.jsmtp.common.RandomProvider;
import com.pchudzik.jsmtp.common.StoppableThread;
import com.pchudzik.jsmtp.common.TimeProvider;
import com.pchudzik.jsmtp.server.command.CommandRegistry;
import com.pchudzik.jsmtp.server.nio.ConnectionsAcceptingServer;
import com.pchudzik.jsmtp.server.nio.pool.MultiConnectionPool;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnectionFactory;
import com.pchudzik.jsmtp.server.nio.pool.client.ConnectionsRegistry;
import lombok.experimental.Builder;

/**
 * Created by pawel on 24.05.14.
 */
@Builder
public class Server {
	private final TimeProvider timeProvider = new TimeProvider();
	private final RandomProvider randomProvider = new RandomProvider();

	private final ServerInstance serverInstance = new ServerInstance();

	private final ServerConfiguration serverConfiguration;
	private final boolean withShutdownHook;
	private final EmailDeliverer emailDeliverer;

	public void start() {
		serverInstance.initialize();
		serverInstance.start();
	}

	public void stop() {
		serverInstance.stop();
	}

	private class ServerInstance {
		private ConnectionsRegistry connectionsRegistry;
		private ConnectionsAcceptingServer server;

		private StoppableThread connectionsRegistryThread;
		private StoppableThread connectionsAcceptingServerThread;
		private MultiConnectionPool connectionPool;

		void initialize() {
			connectionsRegistry = new ConnectionsRegistry(serverConfiguration, timeProvider);
			connectionPool = new MultiConnectionPool(
					serverConfiguration,
					randomProvider,
					new ClientConnectionFactory(timeProvider, connectionsRegistry),
					new SmtpClientHandler(new CommandRegistry(serverConfiguration, emailDeliverer)));
			server = new ConnectionsAcceptingServer(serverConfiguration.getListenAddress(), serverConfiguration.getPort(), connectionPool);

			connectionsRegistryThread = new StoppableThread(connectionsRegistry, "connection registry");
			connectionsAcceptingServerThread = new StoppableThread(server, "connections accepting thread");

			if(withShutdownHook) {
				Runtime.getRuntime().addShutdownHook(new Thread(() -> this.stop()));
			}
		}

		void start() {
			connectionPool.initialize();
			connectionsRegistryThread.start();
			connectionsAcceptingServerThread.start();
		}

		void stop() {
			connectionsAcceptingServerThread.shutdown();
			connectionsRegistryThread.shutdown();
			connectionPool.destroy();
		}
	}
}
