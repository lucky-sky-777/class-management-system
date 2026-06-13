package com.mezon.classmanagement.backend.domain_public.week.service;

import com.mezon.classmanagement.backend.common.util.DateTimeUtils;
import com.mezon.classmanagement.backend.domain_public.week.dto.WeekResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class WeekService {

	public List<WeekResponseDto> getWeekList(int year) {
		List<WeekResponseDto> response = new ArrayList<>();

		int totalWeeks = DateTimeUtils.getTotalWeeks(year);

		Instant now = Instant.now();

		for (int week = 1; week <= totalWeeks; week++) {
			Instant startAt = DateTimeUtils.getWeekStartAt(year, week);
			Instant endAt = DateTimeUtils.getWeekEndAt(year, week);

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

}