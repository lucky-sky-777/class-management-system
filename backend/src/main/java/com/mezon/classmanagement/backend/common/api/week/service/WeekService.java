package com.mezon.classmanagement.backend.common.api.week.service;

import com.mezon.classmanagement.backend.common.constant.DateTimeConstant;
import com.mezon.classmanagement.backend.common.api.week.dto.WeekResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class WeekService {

	private static final ZoneId ZONE_ID = ZoneId.of(DateTimeConstant.TIMEZONE);

	private static final WeekFields WEEK_FIELDS = WeekFields.ISO;

	public List<WeekResponseDto> getWeeks(int year) {
		List<WeekResponseDto> response = new ArrayList<>();

		int totalWeeks = LocalDate
				.of(year, 12, 28)
				.get(WEEK_FIELDS.weekOfWeekBasedYear());

		Instant now = Instant.now();

		for (int week = 1; week <= totalWeeks; week++) {
			Instant startAt = getWeekStartAt(year, week);
			Instant endAt = getWeekEndAt(year, week);

			boolean isCurrentWeek =
					!now.isBefore(startAt) && !now.isAfter(endAt);

			response.add(
					WeekResponseDto.builder()
							.week((long) week)
							.isCurrentWeek(isCurrentWeek)
							.startAt(startAt)
							.endAt(endAt)
							.formattedStartAt(startAt)
							.formattedEndAt(endAt)
							.build()
			);
		}

		return response;
	}

	public Instant getMonthByWeekStartAt() {
		LocalDate now = LocalDate.now(ZONE_ID).minusWeeks(3);

		int year = now.get(WEEK_FIELDS.weekBasedYear());
		int week = now.get(WEEK_FIELDS.weekOfWeekBasedYear());

		return getWeekStartAt(year, week);
	}

	public Instant getWeekStartAtBefore(int weeksBefore) {
		LocalDate date = LocalDate.now(ZONE_ID).minusWeeks(weeksBefore);

		int year = date.get(WEEK_FIELDS.weekBasedYear());
		int week = date.get(WEEK_FIELDS.weekOfWeekBasedYear());

		return getWeekStartAt(year, week);
	}

	public Instant getWeekEndAtBefore(int weeksBefore) {
		LocalDate date = LocalDate.now(ZONE_ID).minusWeeks(weeksBefore);

		int year = date.get(WEEK_FIELDS.weekBasedYear());
		int week = date.get(WEEK_FIELDS.weekOfWeekBasedYear());

		return getWeekEndAt(year, week);
	}

	public Instant getCurrentWeekStartAt() {
		LocalDate now = LocalDate.now(ZONE_ID);

		int year = now.get(WEEK_FIELDS.weekBasedYear());
		int week = now.get(WEEK_FIELDS.weekOfWeekBasedYear());

		return getWeekStartAt(year, week);
	}

	public Instant getCurrentWeekEndAt() {
		LocalDate now = LocalDate.now(ZONE_ID);

		int year = now.get(WEEK_FIELDS.weekBasedYear());
		int week = now.get(WEEK_FIELDS.weekOfWeekBasedYear());

		return getWeekEndAt(year, week);
	}

	private Instant getWeekStartAt(int year, int week) {
		LocalDate startDate = LocalDate.of(year, 1, 4)
				.with(WEEK_FIELDS.weekOfWeekBasedYear(), week)
				.with(WEEK_FIELDS.dayOfWeek(), 1);

		return startDate
				.atStartOfDay(ZONE_ID)
				.toInstant();
	}

	private Instant getWeekEndAt(int year, int week) {
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