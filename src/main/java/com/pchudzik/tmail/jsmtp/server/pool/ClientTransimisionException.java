package com.pchudzik.tmail.jsmtp.server.pool;

import com.pchudzik.tmail.jsmtp.server.SmtpServerException;

/**
 * User: pawel
 * Date: 10.04.14
 * Time: 18:44
 */
public class ClientTransimisionException extends SmtpServerException {
	public ClientTransimisionException() {
	}

	public ClientTransimisionException(String message) {
		super(message);
	}

	public ClientTransimisionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClientTransimisionException(Throwable cause) {
		super(cause);
	}
}
