package com.mezon.classmanagement.backend.common.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class DateTimeUtils {

	public static long currentTimestamp() {
		return Instant.now().toEpochMilli();
	}

	public static long minutesInTimestamp(long minutes) {
		return Instant.now().plus(minutes, ChronoUnit.MINUTES).toEpochMilli();
	}

	public static long daysInTimestamp(long days) {
		return Instant.now().plus(days, ChronoUnit.DAYS).toEpochMilli();
	}

}