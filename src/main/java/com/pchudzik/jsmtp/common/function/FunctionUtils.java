package com.pchudzik.jsmtp.common.function;

import java.util.function.Consumer;

/**
 * Created by pawel on 03.05.14.
 */
public class FunctionUtils {
	public static <T> Consumer<T> uncheckedConsumer(UncheckedConsumer<T> consumer) {
		return (t) -> {
			try {
				consumer.accept(t);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		};
	}
}
