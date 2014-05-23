package com.pchudzik.jsmtp.api;

/**
 * Created by pawel on 23.05.14.
 */
@FunctionalInterface
public interface InputClientHandler {
	void onNewClientConnection(ClientConnectionContext connectionContext);

	default void onClientConnectionError(ClientConnectionContext connectionContext) { }
	default void onClientDisconnect(ClientConnectionContext connectionContext) { }
}
