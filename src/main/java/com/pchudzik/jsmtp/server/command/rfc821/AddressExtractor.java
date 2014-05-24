package com.pchudzik.jsmtp.server.command.rfc821;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.pchudzik.jsmtp.server.command.Command;

/**
 * Created by pawel on 23.04.14.
 */
class AddressExtractor {
	public static InternetAddress getAddress(Command command) throws AddressException {
		final String [] cmdWithArguments = command.getCommandString().split(":", 2);
		if(cmdWithArguments.length < 2) {
			throw new AddressException("Missing email address");
		}
		return new InternetAddress(cmdWithArguments[1]);
	}
}
