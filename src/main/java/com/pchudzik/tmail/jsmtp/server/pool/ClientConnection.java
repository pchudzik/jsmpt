package com.pchudzik.tmail.jsmtp.server.pool;

import java.io.IOException;

/**
 * User: pawel
 * Date: 12.04.14
 * Time: 13:41
 */
public class ClientConnection {
	public void close() throws IOException {

	}

	public boolean isValid() {
		return false;
	}

	public long getLastHeartbeat() {
		return 0;
	}

	public void timeout() throws IOException {

	}

	public void setBroken(Throwable broken) {
	}
}
