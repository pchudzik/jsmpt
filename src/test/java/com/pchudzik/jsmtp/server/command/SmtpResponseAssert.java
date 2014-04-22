package com.pchudzik.jsmtp.server.command;

import org.assertj.core.api.AbstractAssert;

/**
 * Created by pawel on 22.04.14.
 */
class SmtpResponseAssert extends AbstractAssert<SmtpResponseAssert, SmtpResponse> {
	protected SmtpResponseAssert(SmtpResponse actual) {
		super(actual, SmtpResponseAssert.class);
	}

	public static SmtpResponseAssert assertThat(SmtpResponse actual) {
		return new SmtpResponseAssert(actual);
	}

	public SmtpResponseAssert hasSmtpResponse(SmtpResponse smtpResponse) {
		isNotNull();

		if(actual != smtpResponse) {
			failWithMessage("Expected smpt response <%s> but was <%s>", smtpResponse, actual);
		}

		return this;
	}
}
