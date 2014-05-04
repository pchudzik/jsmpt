package com.pchudzik.jsmtp.common;

import java.util.Random;

/**
 * Created by pawel on 04.05.14.
 */
public class RandomProvider {
	private final Random random = new Random();

	public int getNextInt(int limit) {
		return random.nextInt(limit);
	}
}
