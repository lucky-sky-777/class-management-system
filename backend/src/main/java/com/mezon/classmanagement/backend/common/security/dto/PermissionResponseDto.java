package com.mezon.classmanagement.backend.common.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@JsonPropertyOrder(value = {
		"id",
		"code",
		"label"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class PermissionResponseDto {

	@JsonProperty(value = "id")
	Long id;

	@JsonProperty(value = "code")
	String code;

	@JsonProperty(value = "label")
	String label;

}