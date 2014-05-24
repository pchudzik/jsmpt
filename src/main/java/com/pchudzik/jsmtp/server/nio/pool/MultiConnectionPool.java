package com.pchudzik.jsmtp.server.nio.pool;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pchudzik.jsmtp.common.function.FunctionUtils.uncheckSupplier;

import com.pchudzik.jsmtp.common.RandomProvider;
import com.pchudzik.jsmtp.common.StoppableThread;
import com.pchudzik.jsmtp.server.ClientHandler;
import com.pchudzik.jsmtp.server.ServerConfiguration;
import com.pchudzik.jsmtp.server.ServerConfiguration.ConnectionPoolConfiguration;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnectionFactory;

/**
 * Created by pawel on 04.05.14.
 */
public class MultiConnectionPool implements ConnectionPool {
	private final RandomProvider randomProvider;
	private final List<ConnectionPoolElement> connectionPools;
	private final List<StoppableThread> connectionProcessingThreads;
	private final int poolSize;

	public MultiConnectionPool(ServerConfiguration configuration, RandomProvider randomProvider, ClientConnectionFactory clientConnectionFactory, ClientHandler clientHandler) {
		final ConnectionPoolConfiguration poolConfiguration = configuration.getConnectionPoolConfiguration();
		this.randomProvider = randomProvider;
		connectionPools = Stream.generate(uncheckSupplier(() -> new ConnectionPoolElement(configuration, clientConnectionFactory, clientHandler)))
				.limit(poolConfiguration.getConnectionPoolsSize())
				.collect(Collectors.toList());
		connectionProcessingThreads = connectionPools.stream()
				.map((task) -> new StoppableThread(task, "Connection pool elemnet"))
				.collect(Collectors.toList());
		this.poolSize = connectionPools.size();
	}

	@PostConstruct
	public void initialize() {
		connectionProcessingThreads.stream()
				.forEach(thread -> thread.start());
	}

	@PreDestroy
	public void destroy() {
		connectionProcessingThreads.stream()
				.forEach(StoppableThread::shutdown);
	}

	@Override
	public void registerClient(SocketChannel accept) throws ClientRejectedException {
		connectionPools.get(randomPoolElement())
				.registerClient(accept);
	}

	private int randomPoolElement() {
		return randomProvider.getNextInt(poolSize);
	}
}
