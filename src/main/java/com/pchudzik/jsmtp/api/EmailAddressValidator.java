package com.pchudzik.jsmtp.api;

import javax.mail.internet.AddressException;
import java.net.InetAddress;

/**
 * Created by pawel on 23.05.14.
 */
@FunctionalInterface
public interface EmailAddressValidator {
	EmailValidationStatus checkEmail(InetAddress address) throws AddressException;
}
