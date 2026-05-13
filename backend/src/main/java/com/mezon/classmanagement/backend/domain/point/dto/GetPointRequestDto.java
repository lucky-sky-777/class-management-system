package com.mezon.classmanagement.backend.domain.point.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import jakarta.validation.constraints.NotNull;
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
public final class GetPointRequestDto {

	@NotNull
	@JsonProperty(value = "start_at")
	Instant startAt;

	@NotNull
	@JsonProperty(value = "end_at")
	Instant endAt;

}