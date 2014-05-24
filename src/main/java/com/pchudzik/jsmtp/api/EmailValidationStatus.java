package com.pchudzik.jsmtp.api;

import java.net.InetAddress;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailValidationStatus {
	@Getter private final boolean forward;
	@Getter private final InetAddress forwardAddress;

	public static EmailValidationStatus ok(InetAddress address) {
		return new EmailValidationStatus(false, address);
	}

	public static EmailValidationStatus forward(InetAddress address) {
		return new EmailValidationStatus(true, address);
	}
}