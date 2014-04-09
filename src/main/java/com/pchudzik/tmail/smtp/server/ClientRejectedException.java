package com.pchudzik.tmail.smtp.server;

/**
 * User: pawel
 * Date: 06.04.14
 * Time: 18:18
 */
public class ClientRejectedException extends SmtpServerException {
	public ClientRejectedException() {
	}

	public ClientRejectedException(String message) {
		super(message);
	}

	public ClientRejectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClientRejectedException(Throwable cause) {
		super(cause);
	}
}
