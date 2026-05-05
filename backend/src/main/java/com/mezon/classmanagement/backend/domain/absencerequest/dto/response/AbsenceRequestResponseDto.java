package com.mezon.classmanagement.backend.domain.absencerequest.dto.response;

import com.fasterxml.jackson.annotation.*;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.common.constant.DateTimeConstant;
import com.mezon.classmanagement.backend.domain.absencerequest.entity.AbsenceRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@JsonPropertyOrder(value = {
        "id",
        "class_id",
        "user_id",
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

    @JsonFormat(pattern = DateTimeConstant.PATTERN, timezone = DateTimeConstant.TIMEZONE)
    @JsonProperty(value = "created_at")
    Instant createdAt;

}