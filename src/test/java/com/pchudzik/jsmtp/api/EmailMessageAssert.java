package com.pchudzik.jsmtp.api;

import javax.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.assertj.core.api.AbstractAssert;

/**
 * Created by pawel on 24.05.14.
 */
public class EmailMessageAssert extends AbstractAssert<EmailMessageAssert, EmailMessage> {
	protected EmailMessageAssert(EmailMessage actual) {
		super(actual, EmailMessageAssert.class);
	}

	public static EmailMessageAssert assertThat(EmailMessage msg) {
		return new EmailMessageAssert(msg);
	}

	public EmailMessageAssert fromAddress(InternetAddress fromAddress) {
		isNotNull();

		if(!Objects.equals(actual.getFrom(), fromAddress)) {
			failWithMessage("Expected from address %s but was %s", fromAddress, actual.getFrom());
		}

		return this;
	}

	public EmailMessageAssert receipients(Collection<InternetAddress> receipients) {
		isNotNull();

		final Set<InternetAddress> expectedRecipients = new HashSet<>(receipients);
		if(!Objects.equals(actual.getRecipients(), expectedRecipients)) {
			failWithMessage("Expected recipients list\n%s\nbut was\n%s", expectedRecipients, actual.getRecipients());
		}

		return this;
	}

	public EmailMessageAssert dataContains(String messageContent) {
		isNotNull();

		if(!actual.getData().contains(messageContent)) {
			failWithMessage("%s\nwas not found in message\n%s");
		}

		return this;
	}
}
