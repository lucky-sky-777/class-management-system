package com.mezon.classmanagement.backend.domain.point.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@JsonPropertyOrder(value = {
		"rank",
		"week_1_point",
		"week_2_point",
		"week_3_point",
		"week_4_point",
		"group_name",
		"total_point"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class MonthPointRankingResponseDto {

	@JsonProperty(value = "rank")
	Short rank;

	@JsonProperty(value = "week_1_point")
	Short week1Point;

	@JsonProperty(value = "week_2_point")
	Short week2Point;

	@JsonProperty(value = "week_3_point")
	Short week3Point;

	@JsonProperty(value = "week_4_point")
	Short week4Point;

	@JsonProperty(value = "group_name")
	String groupName;

	@JsonProperty(value = "total_point")
	Short totalPoint;

}