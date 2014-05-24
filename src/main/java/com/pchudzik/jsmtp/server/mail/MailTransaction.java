package com.pchudzik.jsmtp.server.mail;

import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

import com.google.common.base.Preconditions;
import com.pchudzik.jsmtp.api.EmailDeliverer;
import com.pchudzik.jsmtp.api.EmailMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Builder;

/**
 * Created by pawel on 16.04.14.
 */
public class MailTransaction {
	@Setter private InternetAddress from;
	private Set<InternetAddress> recipients = newHashSet();
	private UserInput userInput;

	public void reset() {
		from = null;
		recipients = newHashSet();
	}

	public void addRecipient(InternetAddress recipient) {
		recipients.add(recipient);
	}

	public boolean dataInProgress() {
		return userInput != null && !userInput.userInputFinished;
	}

	private void assertUserInputInProgress() {
		Preconditions.checkNotNull(userInput, "Data transaction not started!");
	}

	public void startUserInput() {
		userInput = new UserInput();
	}

	public void addUserData(StringBuilder buffer) {
		assertUserInputInProgress();
		userInput.sb.append(buffer);
	}

	public void userDataFinished(EmailDeliverer deliverer) throws IOException {
		assertUserInputInProgress();
		userInput.userInputFinished = true;
		deliverer.sendEmail(EmailContent.builder()
				.data(userInput.sb.toString())
				.from(from)
				.recipients(recipients)
				.build());
	}

	private static class UserInput {
		boolean userInputFinished = false;
		StringBuffer sb = new StringBuffer();
	}

	@Builder @Getter
	private static class EmailContent implements EmailMessage {
		private final String data;
		private final InternetAddress from;
		private final Collection<InternetAddress> recipients;
	}
}
