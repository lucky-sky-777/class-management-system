package com.mezon.classmanagement.backend.common.security.service;

import com.mezon.classmanagement.backend.common.constant.JwtConstant;
import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.DateTimeUtils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings(value = {WarningConstant.UNUSED})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class JwtService {

	JwtDecoder accessTokenDecoder;
	JwtDecoder refreshTokenDecoder;

	JwtConstant jwtConstant;

	public String generateAccessToken(
			Long userId,
			String username
	) {
		JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(String.valueOf(userId))
				.issuer("tuan.com")
				.issueTime(
						new Date(
								DateTimeUtils.currentTimestamp()
						)
				)
				.expirationTime(
						new Date(
								DateTimeUtils.minutesInTimestamp(15)
						)
				)
				.jwtID(UUID.randomUUID().toString())
				.claim(JwtConstant.CLAIM_USERNAME, username)
				.claim(JwtConstant.CLAIM_TYPE, JwtConstant.TYPE_ACCESS)
				.build();

		return signToken(jwsHeader, jwtClaimsSet);
	}

	public String generateRefreshToken(
			Long userId,
			String username
	) {
		JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(String.valueOf(userId))
				.issuer("tuan.com")
				.issueTime(
						new Date(
								DateTimeUtils.currentTimestamp()
						)
				)
				.expirationTime(
						new Date(
								DateTimeUtils.daysInTimestamp(7)
						)
				)
				.jwtID(UUID.randomUUID().toString())
				.claim(JwtConstant.CLAIM_USERNAME, username)
				.claim(JwtConstant.CLAIM_TYPE, JwtConstant.TYPE_REFRESH)
				.build();

		return signToken(jwsHeader, jwtClaimsSet);
	}

	private String signToken(
			JWSHeader jwsHeader,
			JWTClaimsSet jwtClaimsSet
	) {
		Payload payload = new Payload(jwtClaimsSet.toJSONObject());
		JWSObject jwsObject = new JWSObject(jwsHeader, payload);

		try {
			jwsObject.sign(
					new MACSigner(
							jwtConstant.SIGNER_KEY.getBytes()
					)
			);

			return jwsObject.serialize();
		} catch (JOSEException e) {
			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, "Sign token failed");
		}
	}

	// From Authentication

	public Jwt extractJwtFromAuthentication(Authentication authentication) {
		return ((JwtAuthenticationToken) authentication).getToken();
	}

	public String extractJtiFromAuthentication(Authentication authentication) {
		Jwt jwt = extractJwtFromAuthentication(authentication);
		return extractJtiFromJwt(jwt);
	}

	public Instant extractExpiryFromAuthentication(Authentication authentication) {
		Jwt jwt = extractJwtFromAuthentication(authentication);
		return extractExpiryFromJwt(jwt);
	}

	public Long extractUserIdFromAuthentication(Authentication authentication) {
		Jwt jwt = extractJwtFromAuthentication(authentication);
		return extractUserIdFromJwt(jwt);
	}

	public String extractUsernameFromAuthentication(Authentication authentication) {
		Jwt jwt = extractJwtFromAuthentication(authentication);
		return extractUsernameFromJwt(jwt);
	}

	// From Raw Access Token

	public Jwt extractJwtFromAccessToken(String rawAccessToken) {
		return accessTokenDecoder.decode(rawAccessToken);
	}

	public String extractJtiFromAccessToken(String rawAccessToken) {
		Jwt jwt = extractJwtFromAccessToken(rawAccessToken);
		return extractJtiFromJwt(jwt);
	}

	public Instant extractExpiryFromAccessToken(String rawAccessToken) {
		Jwt jwt = extractJwtFromAccessToken(rawAccessToken);
		return extractExpiryFromJwt(jwt);
	}

	public Long extractUserIdFromAccessToken(String rawAccessToken) {
		Jwt jwt = extractJwtFromAccessToken(rawAccessToken);
		return extractUserIdFromJwt(jwt);
	}

	public String extractUsernameFromAccessToken(String rawAccessToken) {
		Jwt jwt = extractJwtFromAccessToken(rawAccessToken);
		return extractUsernameFromJwt(jwt);
	}

	// From Raw Refresh Token

	public Jwt extractJwtFromRefreshToken(String rawRefreshToken) {
		return refreshTokenDecoder.decode(rawRefreshToken);
	}

	public String extractJtiFromRefreshToken(String rawRefreshToken) {
		Jwt jwt = extractJwtFromRefreshToken(rawRefreshToken);
		return extractJtiFromJwt(jwt);
	}

	public Instant extractExpiryFromRefreshToken(String rawRefreshToken) {
		Jwt jwt = extractJwtFromRefreshToken(rawRefreshToken);
		return extractExpiryFromJwt(jwt);
	}

	public Long extractUserIdFromRefreshToken(String rawRefreshToken) {
		Jwt jwt = extractJwtFromRefreshToken(rawRefreshToken);
		return extractUserIdFromJwt(jwt);
	}

	public String extractUsernameFromRefreshToken(String rawRefreshToken) {
		Jwt jwt = extractJwtFromRefreshToken(rawRefreshToken);
		return extractUsernameFromJwt(jwt);
	}

	// From JWT

	public String extractTokenFromJwt(Jwt jwt) {
		return jwt.getTokenValue();
	}

	public String extractJtiFromJwt(Jwt jwt) {
		return jwt.getId();
	}

	public Instant extractExpiryFromJwt(Jwt jwt) {
		return jwt.getExpiresAt();
	}

	public Long extractUserIdFromJwt(Jwt jwt) {
		return Long.valueOf(Objects.requireNonNull(jwt.getSubject()));
	}

	public String extractUsernameFromJwt(Jwt jwt) {
		return Objects.requireNonNull(jwt.getClaim("username")).toString();
	}

	public static String extractTypeFromJwt(Jwt jwt) {
		return Objects.requireNonNull(jwt.getClaim("type")).toString();
	}

}