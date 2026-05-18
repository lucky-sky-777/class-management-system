package com.mezon.classmanagement.backend.domain.absencerequest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class CreateAbsenceRequestRequestDto {

    @JsonProperty(value = "reason")
    String reason;

    @JsonProperty(value = "from")
    Instant from;

    @JsonProperty(value = "to")
    Instant to;

    @JsonProperty(value = "proof_url")
    String proofUrl;

}