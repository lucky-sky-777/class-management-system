package com.mezon.classmanagement.backend.domain.auth.oauth2.dto;

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
public class ExchangeOAuthAuthorizationCodeRequest {

	@JsonProperty(value = "oauth_authorization_code")
	String oAuthAuthorizationCode;

}