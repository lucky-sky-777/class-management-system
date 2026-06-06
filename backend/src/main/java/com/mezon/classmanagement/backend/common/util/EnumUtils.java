package com.mezon.classmanagement.backend.common.util;

import java.util.Arrays;
import java.util.List;

public final class EnumUtils {

	public static <E extends Enum<E>> List<E> toList(Class<E> enumClass) {
		return Arrays.asList(enumClass.getEnumConstants());
	}

	public static <E extends Enum<E>> List<String> toNameList(Class<E> enumClass) {
		List<E> enumList = Arrays.asList(enumClass.getEnumConstants());
		return enumList.stream()
				.map(Enum::name)
				.toList();
	}

}