package com.mezon.classmanagement.backend.common.util;

import java.security.SecureRandom;

public final class CodeGenerator {

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static String generate(int length) {
		StringBuilder code = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			code.append(
					CHARACTERS.charAt(
							RANDOM.nextInt(CHARACTERS.length())
					)
			);
		}

		return code.toString();
	}

}