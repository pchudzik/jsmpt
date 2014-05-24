package com.pchudzik.jsmtp.server.mail;

import javax.mail.internet.InternetAddress;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

import com.google.common.base.Preconditions;
import lombok.Setter;

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

	public void userDataFinished() {
		assertUserInputInProgress();
		userInput.userInputFinished = true;
	}

	private static class UserInput {
		boolean userInputFinished = false;
		StringBuffer sb = new StringBuffer();
	}
}
