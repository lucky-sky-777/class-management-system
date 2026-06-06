package com.mezon.classmanagement.backend.domain.auth.oauth2.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.mapper.UserProviderMapper;
import com.mezon.classmanagement.backend.domain.auth.oauth2.dto.ExchangeOAuthAuthorizationCodeRequest;
import com.mezon.classmanagement.backend.domain.auth.oauth2.entity.OAuthAuthorization;
import com.mezon.classmanagement.backend.domain.auth.oauth2.repository.OAuthAuthorizationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class OAuthAuthorizationService {

	OAuthAuthorizationRepository oAuthAuthorizationRepository;

	@Transactional
	public OAuthAuthorization create(
			String code,
			String origin,
			String provider,
			String accessToken,
			String refreshToken
	) {
		OAuthAuthorization newOAuthAuthorization = OAuthAuthorization.builder()
				.code(code)
				.origin(origin)
				.provider(UserProviderMapper.toUserProvider(provider))
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();

		return save(newOAuthAuthorization);
	}

	@Transactional
	public OAuthAuthorization exchange(String origin, String provider, ExchangeOAuthAuthorizationCodeRequest request) {
		OAuthAuthorization currentOAuthAuthorization = findByOriginAndCodeOrThrow(origin, request.getOAuthAuthorizationCode());

		if (!Objects.equals(provider.toLowerCase(Locale.ROOT), currentOAuthAuthorization.getProvider().name().toLowerCase(Locale.ROOT))) {
			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Invalid request");
		}
		if (currentOAuthAuthorization.getExpiryDate().isBefore(Instant.now())) {
			throw new GlobalException(GlobalException.Type.INVALID_AUTHENTICATION, "OAuth authorization code expired");
		}
		if (currentOAuthAuthorization.getUsed()) {
			throw new GlobalException(GlobalException.Type.INVALID_AUTHENTICATION, "OAuth authorization code already used");
		}

		currentOAuthAuthorization.setUsed(true);

		return save(currentOAuthAuthorization);
	}

	@Transactional
	public OAuthAuthorization save(OAuthAuthorization oAuthAuthorization) {
		return oAuthAuthorizationRepository.save(oAuthAuthorization);
	}

	@Transactional
	public OAuthAuthorization findByOriginAndCodeOrThrow(String origin, String code) {
		return oAuthAuthorizationRepository
				.findByOriginAndCode(origin, code)
				.orElseThrow(() ->
						new GlobalException(GlobalException.Type.NOT_FOUND, "OAuth authorization code not found")
				);
	}

}