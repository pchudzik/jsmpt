package com.pchudzik.jsmtp.common.function;

import java.util.function.Consumer;
import java.util.function.Supplier;

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

	public static <T> Supplier<T> uncheckedSupplier(UncheckedSupplier<T> supplier) {
		return () -> {
			try {
				return supplier.get();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		};
	}
}
