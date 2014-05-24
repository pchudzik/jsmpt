package com.pchudzik.jsmtp.server;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import com.pchudzik.jsmtp.server.ServerConfiguration.ConnectionPoolConfiguration;
import lombok.SneakyThrows;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by pawel on 06.05.14.
 */
public class MailSendIntegrationTest {
	private final String mailHost = "localhost";
	private final int mailPort = 9099;

	private Server server;

	@SneakyThrows
	@BeforeClass
	public void setupServer() {
		server = Server.builder()
					.serverConfiguration(ServerConfiguration.builder()
							.listenAddress(mailHost)
							.port(mailPort)
							.connectionPoolConfiguration(ConnectionPoolConfiguration.defaults)
							.build())
					.withShutdownHook(false)
					.build();
		server.start();
	}

	@AfterClass
	public void destroyServer() {
		server.stop();
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
