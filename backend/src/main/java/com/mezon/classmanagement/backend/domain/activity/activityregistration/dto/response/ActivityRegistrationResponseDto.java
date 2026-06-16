package com.mezon.classmanagement.backend.domain.activity.activityregistration.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.common.constant.DateTimeConstant;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.entity.ActivityRegistration;
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
		"activity_id",
		"proof_url",
		"created_at",
		"creator_user_id",
		"creator_display_name",
		"creator_avatar_url",
		"status",
		"actor_user_id",
		"actor_display_name",
		"actor_avatar_url"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class ActivityRegistrationResponseDto {

	@JsonProperty(value = "id")
	Long id;
	
	@JsonProperty(value = "class_id")
	Long classId;

	@JsonProperty(value = "activity_id")
	Long activityId;

	@JsonProperty(value = "proof_url")
	String proofUrl;

	@JsonFormat(pattern = DateTimeConstant.PATTERN_FULL_DATETIME, timezone = DateTimeConstant.TIMEZONE)
	@JsonProperty(value = "created_at")
	Instant createdAt;
	
	@JsonProperty(value = "creator_user_id")
	Long creatorUserId;

	@JsonProperty(value = "creator_display_name")
	String creatorDisplayName;

	@JsonProperty(value = "creator_avatar_url")
	String creatorAvatarUrl;
	
	@JsonProperty(value = "status")
	ActivityRegistration.Status status;
	
	@JsonProperty(value = "actor_user_id")
	Long actorUserId;

	@JsonProperty(value = "actor_display_name")
	String actorDisplayName;

	@JsonProperty(value = "actor_avatar_url")
	String actorAvatarUrl;
	
}