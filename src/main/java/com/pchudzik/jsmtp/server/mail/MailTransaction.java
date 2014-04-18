package com.pchudzik.jsmtp.server.mail;

import javax.mail.internet.InternetAddress;

/**
 * Created by pawel on 16.04.14.
 */
public class MailTransaction {
	private InternetAddress from;

	public void reset() {

	}

	public void setFrom(InternetAddress from) {
		this.from = from;
	}
}
