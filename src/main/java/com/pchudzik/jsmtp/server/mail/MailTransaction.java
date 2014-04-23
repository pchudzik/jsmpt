package com.pchudzik.jsmtp.server.mail;

import javax.mail.internet.InternetAddress;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * Created by pawel on 16.04.14.
 */
public class MailTransaction {
	private InternetAddress from;
	private Set<InternetAddress> recipients = newHashSet();

	public void reset() {
		from = null;
		recipients = newHashSet();
	}

	public void setFrom(InternetAddress from) {
		this.from = from;
	}

	public void addRecipient(InternetAddress recipient) {
		recipients.add(recipient);
	}
}
