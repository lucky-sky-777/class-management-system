package com.mezon.classmanagement.backend.common.security.validator;

import com.mezon.classmanagement.backend.common.constant.JwtConstant;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class AccessTokenValidator implements OAuth2TokenValidator<Jwt> {

	@NullMarked
	@Override
	public OAuth2TokenValidatorResult validate(Jwt jwt) {
		String type = JwtService.extractTypeFromJwt(jwt);

		if (!JwtConstant.TYPE_ACCESS.equals(type)) {
			return OAuth2TokenValidatorResult.failure(
					new OAuth2Error(
							"invalid_token",
							"Only access tokens are accepted",
							null
					)
			);
		}

		return OAuth2TokenValidatorResult.success();
	}

}