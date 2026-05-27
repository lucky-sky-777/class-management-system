package com.mezon.classmanagement.backend.common.security.service;

import com.mezon.classmanagement.backend.common.constant.JwtConstant;
import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@SuppressWarnings({WarningConstant.UNUSED})
@Service
@RequiredArgsConstructor
public class JwtService {
	private final JwtDecoder jwtDecoder;
	private final JwtConstant jwtConstant;

	public String generateAccessToken(Long userId, String username) {
		JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(String.valueOf(userId))
				.issuer("tuan.com")
				.issueTime(new Date())
				.expirationTime(new Date(
						Instant.now().plus(15, ChronoUnit.MINUTES).toEpochMilli()
				))
				.jwtID(UUID.randomUUID().toString())
				.claim("type", "access")
				.claim("username", username)
				.build();

		return signToken(jwsHeader, jwtClaimsSet);
	}

	public String generateRefreshToken(Long userId, String username) {
		JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(String.valueOf(userId))
				.issuer("tuan.com")
				.issueTime(new Date())
				.expirationTime(new Date(
						Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli()
				))
				.jwtID(UUID.randomUUID().toString())
				.claim("type", "refresh")
				.claim("username", username)
				.build();

		return signToken(jwsHeader, jwtClaimsSet);
	}

	private String signToken(JWSHeader jwsHeader, JWTClaimsSet jwtClaimsSet) {
		Payload payload = new Payload(jwtClaimsSet.toJSONObject());
		JWSObject jwsObject = new JWSObject(jwsHeader, payload);

		try {
			jwsObject.sign(new MACSigner(jwtConstant.SIGNER_KEY.getBytes()));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			throw new RuntimeException("Cannot create token", e);
		}
	}


	// From Authentication

	public Jwt extractJwt(Authentication authentication) {
		return ((JwtAuthenticationToken) authentication).getToken();
	}

	public String extractJti(Authentication authentication) {
		Jwt jwt = extractJwt(authentication);
		return extractJti(jwt);
	}

	public Instant extractExpiry(Authentication authentication) {
		Jwt jwt = extractJwt(authentication);
		return extractExpiry(jwt);
	}

	public Long extractUserId(Authentication authentication) {
		Jwt jwt = extractJwt(authentication);
		return extractUserId(jwt);
	}

	public String extractUsername(Authentication authentication) {
		Jwt jwt = extractJwt(authentication);
		return extractUsername(jwt);
	}

	// From Raw Token

	public Jwt extractJwt(String rawToken) {
		return jwtDecoder.decode(rawToken);
	}

	public String extractJti(String rawToken) {
		Jwt jwt = extractJwt(rawToken);
		return extractJti(jwt);
	}

	public Instant extractExpiry(String rawToken) {
		Jwt jwt = extractJwt(rawToken);
		return extractExpiry(jwt);
	}

	public Long extractUserId(String rawToken) {
		Jwt jwt = extractJwt(rawToken);
		return extractUserId(jwt);
	}

	public String extractUsername(String rawToken) {
		Jwt jwt = extractJwt(rawToken);
		return extractUsername(jwt);
	}

	// From JWT

	public String extractToken(Jwt jwt) {
		return jwt.getTokenValue();
	}

	public String extractJti(Jwt jwt) {
		return jwt.getId();
	}

	public Instant extractExpiry(Jwt jwt) {
		return jwt.getExpiresAt();
	}

	public Long extractUserId(Jwt jwt) {
		return Long.valueOf(jwt.getSubject());
	}

	public String extractUsername(Jwt jwt) {
		return jwt.getClaim("username").toString();
	}

}