package com.mezon.classmanagement.backend.domain.absencerequest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
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

    //@FutureOrPresent
    @JsonProperty(value = "from")
    Instant from;

    //@Future
    @JsonProperty(value = "to")
    Instant to;

    @JsonProperty(value = "proof_url")
    String proofUrl;

}