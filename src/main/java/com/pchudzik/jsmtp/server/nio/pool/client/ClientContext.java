package com.pchudzik.jsmtp.server.nio.pool.client;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;

/**
 * Created by pawel on 16.04.14.
 */
public class ClientContext {
	private final Map<String, List<Object>> contextObjects = Maps.newConcurrentMap();

	ClientContext() { }

	public <T> List<T> getObjects(String name) {
		final List<Object> result = contextObjects.get(name);
		return result != null
				? Collections.unmodifiableList((List<T>)result)
				: Collections.<T>emptyList();
	}

	public <T> Optional<T> getObject(String name) {
		List<T> result = getObjects(name);
		return Optional.ofNullable(Iterables.getFirst(result, null));
	}

	public <T> ClientContext put(String name, T ... objects) {
		return put(name, Arrays.asList(objects));
	}

	public synchronized <T> ClientContext put(String name, List<T> objects) {
		List<Object> registeredObjects = contextObjects.get(name);
		if(registeredObjects == null) {
			registeredObjects = Lists.newLinkedList();
			contextObjects.put(name, registeredObjects);
		}
		registeredObjects.clear();
		registeredObjects.addAll(objects);
		return this;
	}
}
