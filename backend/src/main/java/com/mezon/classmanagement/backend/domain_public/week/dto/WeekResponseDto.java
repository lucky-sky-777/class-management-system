package com.mezon.classmanagement.backend.domain_public.week.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.common.constant.DateTimeConstant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@JsonPropertyOrder(value = {
		"week",
		"start_at",
		"end_at"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class WeekResponseDto {

	@JsonProperty(value = "week")
	Long week;

	@JsonProperty(value = "is_current_week")
	Boolean isCurrentWeek;

	@JsonProperty(value = "start_at")
	Instant startAt;

	@JsonProperty(value = "end_at")
	Instant endAt;

	@JsonFormat(pattern = DateTimeConstant.PATTERN_DATE_ONLY, timezone = DateTimeConstant.TIMEZONE)
	@JsonProperty(value = "formatted_start_at")
	Instant formattedStartAt;

	@JsonFormat(pattern = DateTimeConstant.PATTERN_DATE_ONLY, timezone = DateTimeConstant.TIMEZONE)
	@JsonProperty(value = "formatted_end_at")
	Instant formattedEndAt;

}