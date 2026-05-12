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

	public List<WeekResponseDto> getWeeks(int year) {

		List<WeekResponseDto> result = new ArrayList<>();

		WeekFields weekFields = WeekFields.ISO;

		int totalWeeks = LocalDate.of(year, 12, 28)
				.get(weekFields.weekOfWeekBasedYear());

		for (int week = 1; week <= totalWeeks; week++) {

			LocalDate startDate = LocalDate.of(year, 1, 4)
					.with(weekFields.weekOfWeekBasedYear(), week)
					.with(weekFields.dayOfWeek(), 1);

			LocalDate endDate = startDate.plusDays(6);

			Instant startAt = startDate
					.atStartOfDay(ZoneId.of(DateTimeConstant.TIMEZONE))
					.toInstant();

			Instant endAt = endDate
					.plusDays(1)
					.atStartOfDay(ZoneId.of(DateTimeConstant.TIMEZONE))
					.minusNanos(1)
					.toInstant();

			result.add(new WeekResponseDto(
					(long) week,
					startAt,
					endAt
			));
		}

		return result;
	}

}