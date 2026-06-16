package com.mezon.classmanagement.backend.domain.auth.dto.signin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.common.constant.AuthConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public final class SignInRequestDto {

    @JsonProperty(value = "username")
    String username;

    @NotBlank
    //@Size(min = AuthConstant.MIN_PASSWORD_LENGTH, max = AuthConstant.MAX_PASSWORD_LENGTH)
    @JsonProperty(value = "password")
    String password;

}