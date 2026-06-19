package com.mezon.classmanagement.backend.domain.auth.dto.changepassword;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mezon.classmanagement.backend.common.annotation.DTO;
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
public class ChangePasswordRequestDto {

	@JsonProperty(value = "old_password")
	String oldPassword;

	@JsonProperty(value = "new_password")
	String newPassword;

}