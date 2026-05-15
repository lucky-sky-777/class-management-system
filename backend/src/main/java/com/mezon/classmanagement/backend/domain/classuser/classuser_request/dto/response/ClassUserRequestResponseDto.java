package com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.common.constant.DateTimeConstant;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.entity.ClassUserRequest;
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
		"user_id",
		"status",
		"actor_user_id",
		"acted_at",
		"created_at"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class ClassUserRequestResponseDto {

	@JsonProperty(value = "id")
	Long id;

	@JsonProperty(value = "class_id")
	Long classId;

	@JsonProperty(value = "user_id")
	Long userId;

	@JsonProperty(value = "user_display_name")
	String user_display_name;

	@JsonProperty(value = "user_avatar_url")
	String user_avatar_url;

	@JsonProperty(value = "message")
	String message;

	@JsonProperty(value = "status")
	ClassUserRequest.Status status;

	@JsonProperty(value = "actor_user_id")
	Long actorUserId;

	@JsonFormat(pattern = DateTimeConstant.PATTERN_FULL_DATETIME, timezone = DateTimeConstant.TIMEZONE)
	@JsonProperty(value = "acted_at")
	Instant actedAt;

	@JsonFormat(pattern = DateTimeConstant.PATTERN_FULL_DATETIME, timezone = DateTimeConstant.TIMEZONE)
	@JsonProperty(value = "created_at")
	Instant createdAt;

}