package com.pchudzik.jsmtp.server.command;

/**
 * Created by pawel on 14.04.14.
 */
public enum SmtpResponse {
	HELLO(220),
	OK(250),
	CLOSE(221),

	MAIL_INPUT_START(354),

	SERVICE_UNAVAILABLE(421),

	MAIL_BOX_NOT_AVAILABLE(553),
	TRANSACTION_FAILED(554)
	;

	private final int code;

	private SmtpResponse(int code) {
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
