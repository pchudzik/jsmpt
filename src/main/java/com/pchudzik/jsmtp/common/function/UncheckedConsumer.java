package com.pchudzik.jsmtp.common.function;

/**
 * Created by pawel on 03.05.14.
 */
@FunctionalInterface
public interface UncheckedConsumer<T> {
	void accept(T t) throws Exception;
}
