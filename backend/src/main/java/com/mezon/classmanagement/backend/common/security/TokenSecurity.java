package com.mezon.classmanagement.backend.common.security;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.InvalidatedAccessTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@SuppressWarnings(value = {WarningConstant.UNUSED})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component("TokenSecurity")
public class TokenSecurity {

	AuthService authService;
	JwtService jwtService;
	InvalidatedAccessTokenService invalidatedAccessTokenService;

	public boolean isValid() {
		Authentication authentication = authService.getAuthentication();
		String jti = jwtService.extractJtiFromAuthentication(authentication);

		return isValid(jti);
	}

	private boolean isValid(String jti) {
		if (invalidatedAccessTokenService.isInvalidated(jti)) {
			throw new GlobalException(GlobalException.Type.INVALID_AUTHENTICATION, "Unauthorized");
		}

		return true;
	}

}