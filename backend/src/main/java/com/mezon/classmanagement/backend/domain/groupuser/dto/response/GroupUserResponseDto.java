package com.mezon.classmanagement.backend.domain.groupuser.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.common.constant.DateTimeConstant;
import com.mezon.classmanagement.backend.domain.groupuser.entity.GroupUser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@JsonPropertyOrder(value = {
		"id",
		"class_id",
		"group_id",
		"group_name",
		"user_id",
		"user_display_name",
		"role",
		"desk",
		"desk_position"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class GroupUserResponseDto {

	@JsonProperty(value = "id")
	Long id;

	@JsonProperty(value = "class_id")
	Long classId;

	@JsonProperty(value = "group_id")
	Long groupId;

	@JsonProperty(value = "group_name")
	String groupName;

	@JsonProperty(value = "user_id")
	Long userId;

	@JsonProperty(value = "user_display_name")
	String userDisplayName;

	@JsonProperty(value = "role")
	GroupUser.Role role;

	@JsonProperty(value = "desk")
	Short desk;

	@JsonProperty(value = "desk_position")
	Short deskPosition;

	@JsonFormat(pattern = DateTimeConstant.PATTERN, timezone = DateTimeConstant.TIMEZONE)
	@JsonProperty(value = "joined_at")
	Instant joinedAt;

}