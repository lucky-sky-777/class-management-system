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

import java.util.List;

@JsonPropertyOrder(value = {
		"roles",
		"permissions"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class ClassRoleAndPermissionListResponseDto {

	@JsonProperty(value = "roles")
	List<ClassRoleOrPermissionResponseDto> roles;

	@JsonProperty(value = "permissions")
	List<ClassRoleOrPermissionResponseDto> permissions;

}