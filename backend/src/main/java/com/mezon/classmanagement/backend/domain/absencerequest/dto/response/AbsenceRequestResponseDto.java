package com.mezon.classmanagement.backend.domain.absencerequest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.common.constant.DateTimeConstant;
import com.mezon.classmanagement.backend.domain.absencerequest.entity.AbsenceRequest;
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
        "reason",
        "from",
        "to",
        "proof_url",
        "user_id",
        "user_display_name",
        "user_avatar_url",
        "created_at",
        "status",
        "actor_user_id",
        "actor_user_display_name",
        "actor_user_avatar_url",
        "acted_at"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class AbsenceRequestResponseDto {

    @JsonProperty(value = "id")
    Long id;

    @JsonProperty(value = "class_id")
    Long classId;

    @JsonProperty(value = "reason")
    String reason;

    @JsonProperty(value = "from")
    Instant from;

    @JsonProperty(value = "to")
    Instant to;

    @JsonProperty(value = "proof_url")
    String proofUrl;

    @JsonProperty(value = "user_id")
    Long creatorUserId;

    @JsonProperty(value = "user_display_name")
    String creatorUserDisplayName;

    @JsonProperty(value = "user_avatar_url")
    String creatorUserAvatarUrl;

    @JsonFormat(pattern = DateTimeConstant.PATTERN_FULL_DATETIME, timezone = DateTimeConstant.TIMEZONE)
    @JsonProperty(value = "created_at")
    Instant createdAt;

    @JsonProperty(value = "status")
    AbsenceRequest.Status status;

    @JsonProperty(value = "actor_user_id")
    Long actorUserId;

    @JsonProperty(value = "actor_user_display_name")
    String actorUserDisplayName;

    @JsonProperty(value = "actor_user_avatar_url")
    String actorUserAvatarUrl;

    @JsonFormat(pattern = DateTimeConstant.PATTERN_FULL_DATETIME, timezone = DateTimeConstant.TIMEZONE)
    @JsonProperty(value = "acted_at")
    Instant actedAt;

}