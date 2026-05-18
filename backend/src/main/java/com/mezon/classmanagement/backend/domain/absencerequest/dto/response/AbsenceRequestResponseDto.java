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
        "user_id",
        "user_display_name",
        "user_avatar_url",
        "reason",
        "from",
        "to",
        "proof_url",
        "status",
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
public final class AbsenceRequestResponseDto {

    @JsonProperty(value = "id")
    Long id;

    @JsonProperty(value = "class_id")
    Long classId;

    @JsonProperty(value = "user_id")
    Long userId;

    @JsonProperty(value = "user_display_name")
    String userDisplayName;

    @JsonProperty(value = "user_avatar_url")
    String userAvatarUrl;

    @JsonProperty(value = "reason")
    String reason;

    @JsonProperty(value = "from")
    Instant from;

    @JsonProperty(value = "to")
    Instant to;

    @JsonProperty(value = "proof_url")
    String proofUrl;

    @JsonProperty(value = "status")
    AbsenceRequest.Status status;

    @JsonFormat(pattern = DateTimeConstant.PATTERN_FULL_DATETIME, timezone = DateTimeConstant.TIMEZONE)
    @JsonProperty(value = "created_at")
    Instant createdAt;

}