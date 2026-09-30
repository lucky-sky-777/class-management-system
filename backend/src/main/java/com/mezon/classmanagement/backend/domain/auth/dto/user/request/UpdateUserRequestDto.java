package com.mezon.classmanagement.backend.domain.auth.dto.user.request;

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

@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public class UpdateUserRequestDto {

	@JsonProperty(value = "display_name")
	String displayName;

	@JsonProperty(value = "avatar_url")
	String avatarURl;

	@JsonProperty(value = "school")
	String school;

	@JsonProperty(value = "major")
	String major;

}