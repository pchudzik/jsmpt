package com.pchudzik.jsmtp.server;

/**
 * Created by pawel on 14.04.14.
 */
public class ServerConfiguration {
	private String listenAddress;

	public String getListenAddress() {
		return listenAddress;
	}

	public ServerConfiguration setListenAddress(String listenAddress) {
		this.listenAddress = listenAddress;
		return this;
	}
}
