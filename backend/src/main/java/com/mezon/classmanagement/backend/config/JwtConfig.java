package com.mezon.classmanagement.backend.config;

import com.mezon.classmanagement.backend.common.constant.JwtConstant;
import com.mezon.classmanagement.backend.common.security.validator.AccessTokenValidator;
import com.mezon.classmanagement.backend.common.security.validator.RefreshTokenValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Configuration
public class JwtConfig {

	AccessTokenValidator accessTokenValidator;
	RefreshTokenValidator refreshTokenValidator;

	JwtConstant jwtConstant;

	@Bean(name = "accessTokenDecoder")
	public JwtDecoder accessTokenDecoder() {
		return jwtDecoder(accessTokenValidator);
	}

	@Bean(name = "refreshTokenDecoder")
	public JwtDecoder refreshTokenDecoder() {
		return jwtDecoder(refreshTokenValidator);
	}

	private JwtDecoder jwtDecoder(OAuth2TokenValidator<Jwt> oAuth2TokenValidator) {
		SecretKeySpec secretKeySpec = new SecretKeySpec(jwtConstant.SIGNER_KEY.getBytes(), "HmacSHA512");

		NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder
				.withSecretKey(secretKeySpec)
				.macAlgorithm(MacAlgorithm.HS512)
				.build();

		OAuth2TokenValidator<Jwt> validator =
				new DelegatingOAuth2TokenValidator<>(
						JwtValidators.createDefault(),
						oAuth2TokenValidator
				);

		nimbusJwtDecoder.setJwtValidator(validator);

		return nimbusJwtDecoder;
	}

}