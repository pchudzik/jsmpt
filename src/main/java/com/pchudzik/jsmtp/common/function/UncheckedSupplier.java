package com.pchudzik.jsmtp.common.function;

/**
 * Created by pawel on 04.05.14.
 */
public interface UncheckedSupplier<T> {
	T get() throws Exception;
}
