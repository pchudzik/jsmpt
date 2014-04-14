package com.pchudzik.jsmtp.server.command;

/**
 * Created by pawel on 14.04.14.
 */
public enum SmtpResponseImpl {
	OK(250),
	SERVICE_UNAVAILABLE(421);

	private final int code;

	private SmtpResponseImpl(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return Integer.toString(code);
	}
}
