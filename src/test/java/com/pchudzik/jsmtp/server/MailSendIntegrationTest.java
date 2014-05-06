package com.pchudzik.jsmtp.server;

import com.pchudzik.jsmtp.common.RandomProvider;
import com.pchudzik.jsmtp.common.StoppableThread;
import com.pchudzik.jsmtp.common.TimeProvider;
import com.pchudzik.jsmtp.server.command.rfc821.CommandRegistry;
import com.pchudzik.jsmtp.server.nio.ConnectionsAcceptingServer;
import com.pchudzik.jsmtp.server.nio.pool.ConnectionPoolConfiguration;
import com.pchudzik.jsmtp.server.nio.pool.ConnectionsRegistry;
import com.pchudzik.jsmtp.server.nio.pool.MultiConnectionPool;
import com.pchudzik.jsmtp.server.nio.pool.client.ClientConnectionFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by pawel on 06.05.14.
 */
public class MailSendIntegrationTest {
	private final String mailHost = "localhost";
	private final int mailPort = 9099;

	private StoppableThread connectionsRegistryThread;
	private StoppableThread connectionsAcceptingServerThread;
	private MultiConnectionPool connectionPool;

	@BeforeClass
	public void setupServer() {
		final TimeProvider timeProvider = new TimeProvider();
		final RandomProvider randomProvider = new RandomProvider();

		final ServerConfiguration serverConfiguration = new ServerConfiguration()
				.setListenAddress(mailHost);

		final CommandRegistry commandRegistry = new CommandRegistry(serverConfiguration);
		final ConnectionsRegistry connectionsRegistry = new ConnectionsRegistry(timeProvider);
		connectionPool = new MultiConnectionPool(
				randomProvider,
				new ConnectionPoolConfiguration("cnnection pool").setConnectionPoolsSize(5),
				new ClientConnectionFactory(timeProvider, connectionsRegistry),
				new SmtpClientHandler(commandRegistry));
		final ConnectionsAcceptingServer server = new ConnectionsAcceptingServer(mailHost, mailPort, connectionPool);
		connectionPool.initialize();
		connectionsRegistryThread = new StoppableThread(connectionsRegistry, "connection registry");
		connectionsAcceptingServerThread = new StoppableThread(server, "connections accepting thread");

		connectionsRegistryThread.start();
		connectionsAcceptingServerThread.start();
	}

	@AfterClass
	public void destroyServer() {
		connectionsAcceptingServerThread.shutdown();
		connectionsRegistryThread.shutdown();
		connectionPool.destroy();
	}

	@Test
	public void shouldSendEmail() throws Exception {
		final Properties props = new Properties();
		props.put("mail.smtp.host", mailHost);
		props.put("mail.smtp.port", mailPort);

		final Session session = Session.getDefaultInstance(props);

		final Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("from@example.com"));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress("to@example.com"));
		msg.setSubject("subject");
		msg.setText("content");

		Transport.send(msg);
	}
}
