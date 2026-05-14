package com.mezon.classmanagement.backend.domain.fund.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.domain.fund.entity.Fund;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class CreateFundRequestDto {

	@JsonProperty(value = "type")
	Fund.Type type;

	@JsonProperty(value = "amount")
	Long amount;

	@JsonProperty(value = "title")
	String title;

	@JsonProperty(value = "description")
	String description;

}