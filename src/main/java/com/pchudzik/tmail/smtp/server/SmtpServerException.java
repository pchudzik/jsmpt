package com.pchudzik.tmail.smtp.server;

/**
 * User: pawel
 * Date: 06.04.14
 * Time: 18:17
 */
public class SmtpServerException extends Exception {
	public SmtpServerException() { }

	public SmtpServerException(String message) {
		super(message);
	}

	public SmtpServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public SmtpServerException(Throwable cause) {
		super(cause);
	}
}
