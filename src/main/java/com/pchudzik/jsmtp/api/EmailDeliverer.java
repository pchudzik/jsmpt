package com.pchudzik.jsmtp.api;

import java.io.IOException;

/**
 * Created by pawel on 23.05.14.
 */
@FunctionalInterface
public interface EmailDeliverer {
	void sendEmail(EmailMessage message) throws IOException;
}
