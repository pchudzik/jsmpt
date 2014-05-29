package com.pchudzik.jsmtp.common.function;

import java.util.Objects;

import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.AbstractAssert;

/**
 * Created by pawel on 29.05.14.
 */
public class ObjectAssert<T> extends AbstractAssert<ObjectAssert<T>, T> {
	protected ObjectAssert(T actual) {
		super(actual, ObjectAssert.class);
	}

	public static <T> ObjectAssert<T> assertThat(T object) {
		return new ObjectAssert<>(object);
	}

	@SneakyThrows
	public <V> ObjectAssert<T> isEqual(ValueProvider<T, V> valueProvider, V expectedValue, MessageProvider<T, V> messageProvider) {
		V actualValue = valueProvider.getValue(actual);
		if(!Objects.equals(actualValue, expectedValue)) {
			failWithMessage(messageProvider.getMessage(actual, actualValue, expectedValue));
		}
		return this;
	}

	public <V> ObjectAssert<T> isEqual(ValueProvider<T, V> valueProvider, V value) {
		return isEqual(
				valueProvider,
				value,
				(actual, expected) -> "Expected value\n<" + expected +">\nbut was\n<" + actual + ">");
	}

	@SneakyThrows
	public ObjectAssert<T> is(SimpleCondition<T> condition, boolean expectedValue) {
		final boolean matches = condition.matches(actual);
		if(matches != expectedValue) {
			failWithMessage("Expected <%s> to be <%s>", matches, expectedValue);
		}
		return this;
	}

	public ObjectAssert<T> isTrue(SimpleCondition<T> condition) {
		return is(condition, true);
	}

	public ObjectAssert<T> isFalse(SimpleCondition<T> condition) {
		return is(condition, false);
	}

	@SneakyThrows
	public ObjectAssert<T> contains(ValueProvider<T, String> valueProvider, String stringToFind, MessageProvider<T, String> messageProvider) {
		final String value = valueProvider.getValue(actual);
		if(!StringUtils.contains(value, stringToFind)) {
			failWithMessage(messageProvider.getMessage(actual, value, stringToFind));
		}

		return this;
	}

	public ObjectAssert<T> contains(ValueProvider<T, String> valueProvider, String stringToFind) {
		return contains(
				valueProvider,
				stringToFind,
				(actual, expected) -> "Expected\n<" + actual + ">\nto contain\n<" + stringToFind + ">");
	}

	@FunctionalInterface
	public interface SimpleCondition<T> {
		boolean matches(T object) throws Exception;
	}

	@FunctionalInterface
	public interface ValueProvider<T, V> {
		V getValue(T object) throws Exception;
	}

	@FunctionalInterface
	public interface MessageProvider<T, V> {
		String getMessage(V actual, V expected) throws Exception;
		default String getMessage(T object, V actual, V expected) throws Exception {
			return getMessage(actual, expected);
		}
	}
}
