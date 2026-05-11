package com.mezon.classmanagement.backend.domain.seat.dto;

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

import java.util.LinkedHashMap;
import java.util.List;

@JsonPropertyOrder(value = {
		"name",
		"desks"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class GroupSeatResponseDto {

	@JsonProperty(value = "name")
	String name;

	@JsonProperty(value = "desks")
	List<LinkedHashMap<Short /* desk */, DeskSeatResponseDto>> desks;

}