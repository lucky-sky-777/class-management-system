package com.mezon.classmanagement.backend.domain.seat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.domain.groupuser.entity.GroupUser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@JsonPropertyOrder(value = {
		"user_id",
		"user_display_name",
		"user_avatar_url",
		"attendance_status"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class DeskPositionSeatResponseDto {

	@JsonProperty(value = "user_id")
	Long userId;

	@JsonProperty(value = "user_display_name")
	String userDisplayName;

	@JsonProperty(value = "user_avatar_url")
	String userAvatarUrl;

	@JsonProperty(value = "attendance_status")
	GroupUser.AttendanceStatus attendanceStatus;

}