package com.pchudzik.jsmtp.server;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static com.google.common.collect.Iterables.getOnlyElement;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.pchudzik.jsmtp.api.EmailMessage;
import com.pchudzik.jsmtp.common.function.ObjectAssert;
import com.pchudzik.jsmtp.common.function.ObjectAssert.ValueProvider;
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
	private EmailMessage latestMessage;

	@SneakyThrows
	@BeforeClass
	public void setupServer() {
		server = Server.builder()
					.serverConfiguration(ServerConfiguration.builder()
							.listenAddress(mailHost)
							.port(mailPort)
							.connectionPoolConfiguration(ConnectionPoolConfiguration.defaults)
							.build())
					.emailDeliverer(message -> latestMessage = message)
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
		final String fromAddress = "from@example.com";
		final String toAddress = "to@example.com";
		final String data = "message content";

		final Properties props = new Properties();
		props.put("mail.smtp.host", mailHost);
		props.put("mail.smtp.port", mailPort);

		final Session session = Session.getDefaultInstance(props);

		final Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(fromAddress));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
		msg.setSubject("subject");
		msg.setText(data);

		Transport.send(msg);

		ObjectAssert.assertThat(latestMessage)
				.isEqual(EmailMessage::getFrom, new InternetAddress(fromAddress))
				.contains(EmailMessage::getData, data)
				.isEqual(
						m -> getOnlyElement(m.getRecipients()),
						new InternetAddress(toAddress));
	}
}
