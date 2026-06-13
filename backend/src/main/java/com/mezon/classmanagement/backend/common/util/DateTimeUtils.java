package com.mezon.classmanagement.backend.common.util;

import com.mezon.classmanagement.backend.common.constant.DateTimeConstant;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;

public final class DateTimeUtils {

	///

	public static long currentTimestamp() {
		return Instant.now().toEpochMilli();
	}

	public static long minutesInTimestamp(long minutes) {
		return Instant.now().plus(minutes, ChronoUnit.MINUTES).toEpochMilli();
	}

	public static long daysInTimestamp(long days) {
		return Instant.now().plus(days, ChronoUnit.DAYS).toEpochMilli();
	}

	///

	private static final ZoneId ZONE_ID = ZoneId.of(DateTimeConstant.TIMEZONE);

	private static final WeekFields WEEK_FIELDS = WeekFields.ISO;

	///

	public static int getTotalWeeks(int year) {
		return LocalDate
				.of(year, 12, 28)
				.get(WEEK_FIELDS.weekOfWeekBasedYear());
	}

	///

	public static Instant getWeekStartAtBefore(
			Instant instant,
			int weeksBefore
	) {
		LocalDate date = instant
				.atZone(ZONE_ID)
				.toLocalDate()
				.minusWeeks(weeksBefore);

		int year = date.get(WEEK_FIELDS.weekBasedYear());
		int week = date.get(WEEK_FIELDS.weekOfWeekBasedYear());

		return getWeekStartAt(year, week);
	}

	public static Instant getWeekEndAtBefore(
			Instant instant,
			int weeksBefore
	) {
		LocalDate date = instant
				.atZone(ZONE_ID)
				.toLocalDate()
				.minusWeeks(weeksBefore);

		int year = date.get(WEEK_FIELDS.weekBasedYear());
		int week = date.get(WEEK_FIELDS.weekOfWeekBasedYear());

		return getWeekEndAt(year, week);
	}

	///

	public static Instant getWeekStartAtBefore(int weeksBefore) {
		LocalDate date = LocalDate
				.now(ZONE_ID)
				.minusWeeks(weeksBefore);

		int year = date.get(WEEK_FIELDS.weekBasedYear());
		int week = date.get(WEEK_FIELDS.weekOfWeekBasedYear());

		return getWeekStartAt(year, week);
	}

	public static Instant getWeekEndAtBefore(int weeksBefore) {
		LocalDate date = LocalDate
				.now(ZONE_ID)
				.minusWeeks(weeksBefore);

		int year = date.get(WEEK_FIELDS.weekBasedYear());
		int week = date.get(WEEK_FIELDS.weekOfWeekBasedYear());

		return getWeekEndAt(year, week);
	}

	///

	public static Instant getCurrentWeekStartAt() {
		LocalDate now = LocalDate.now(ZONE_ID);

		int year = now.get(WEEK_FIELDS.weekBasedYear());
		int week = now.get(WEEK_FIELDS.weekOfWeekBasedYear());

		return getWeekStartAt(year, week);
	}

	public static Instant getCurrentWeekEndAt() {
		LocalDate now = LocalDate.now(ZONE_ID);

		int year = now.get(WEEK_FIELDS.weekBasedYear());
		int week = now.get(WEEK_FIELDS.weekOfWeekBasedYear());

		return getWeekEndAt(year, week);
	}

	///

	public static Instant getWeekStartAt(
			int year,
			int week
	) {
		LocalDate startDate = LocalDate.of(year, 1, 4)
				.with(WEEK_FIELDS.weekOfWeekBasedYear(), week)
				.with(WEEK_FIELDS.dayOfWeek(), 1);

		return startDate
				.atStartOfDay(ZONE_ID)
				.toInstant();
	}

	public static Instant getWeekEndAt(
			int year,
			int week
	) {
		LocalDate endDate = LocalDate.of(year, 1, 4)
				.with(WEEK_FIELDS.weekOfWeekBasedYear(), week)
				.with(WEEK_FIELDS.dayOfWeek(), 7);

		return endDate
				.plusDays(1)
				.atStartOfDay(ZONE_ID)
				.minusNanos(1)
				.toInstant();
	}

}