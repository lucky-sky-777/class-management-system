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
			jwsObject.sign(new MACSigner(JwtConstant.SIGNER_KEY.getBytes()));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			throw new RuntimeException("Cannot create token", e);
		}
	}

	public Jwt getJwt(Authentication authentication) {
		return ((JwtAuthenticationToken) authentication).getToken();
	}

//	public Long extractUserId(Authentication authentication) {
//		Jwt jwt = getJwt(authentication);
//		return Long.valueOf(jwt.getClaim("user_id").toString());
//	}

	public Long extractUserId(Authentication authentication) {
		Jwt jwt = getJwt(authentication);
		return Long.valueOf(jwt.getSubject());
	}

//	public String extractUsername(Authentication authentication) {
//		Jwt jwt = getJwt(authentication);
//		return jwt.getSubject();
//	}

	public String extractUsername(Authentication authentication) {
		Jwt jwt = getJwt(authentication);
		return jwt.getClaim("username").toString();
	}

	public String extractJti(Authentication authentication) {
		Jwt jwt = getJwt(authentication);
		return jwt.getId();
	}

	public String extractJti(String rawToken) {
		Jwt jwt = jwtDecoder.decode(rawToken);
		return jwt.getId();
	}

	public Instant extractExpiry(String rawToken) {
		Jwt jwt = jwtDecoder.decode(rawToken);
		return jwt.getExpiresAt();
	}

	public Jwt parseAndValidate(String rawToken) {
		return jwtDecoder.decode(rawToken);  // Spring tự validate signature + expiry
	}

	// Overload: nhận Jwt, trả về userId
	public Long extractUserId(Jwt jwt) {
		return Long.valueOf(jwt.getSubject());
	}

	// Overload: nhận Jwt, trả về username
	public String extractUsername(Jwt jwt) {
		return jwt.getClaim("username").toString();
	}
}