package com.pchudzik.jsmtp.api;

import javax.mail.internet.InternetAddress;
import java.util.Collection;

/**
 * Created by pawel on 23.05.14.
 */
public interface EmailMessage {
	String getData();
	InternetAddress getFrom();
	Collection<InternetAddress> getRecipients();
}
