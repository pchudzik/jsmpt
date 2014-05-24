package com.pchudzik.jsmtp.server.nio.pool.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by pawel on 17.04.14.
 */
public class ClientContextTest {
	private final String key = "anyKey";
	private ClientContext context;

	@BeforeMethod
	public void setupContext() {
		context = new ClientContext();
	}

	@Test
	public void shouldClearPreviouslyStoredObjectsOnPut() {
		context.put(key, "object");

		context.put(key, "otherObject");

		assertThat(context.getObjects(key))
				.containsOnly("otherObject");
	}

	@Test
	public void shouldReturnEmptyOptionalWhenObjectIsMissing() {
		assertThat(context.getObject(key).isPresent()).isFalse();
	}

	@Test
	public void shouldReturnEmptyListWhenObjectsAreNotSet() {
		assertThat(context.getObjects(key)).isEmpty();
	}

	@Test
	public void shouldReturnOptionalFromObjectWhenObjectIsPresent() {
		context.put(key, "object");

		assertThat(context.getObject(key).isPresent()).isTrue();
	}
}
