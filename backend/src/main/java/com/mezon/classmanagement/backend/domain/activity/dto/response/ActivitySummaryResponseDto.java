package com.mezon.classmanagement.backend.domain.activity.dto.response;

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
		"user_id",
		"user_display_name",
		"user_avatar_url",
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
public class ActivitySummaryResponseDto {

	@JsonProperty(value = "rank")
	Short rank;

	@JsonProperty(value = "user_id")
	Long userId;

	@JsonProperty(value = "user_display_name")
	String userDisplayName;

	@JsonProperty(value = "user_avatar_url")
	String userAvatarUrl;

	@JsonProperty(value = "total_point")
	Short totalPoint;

}