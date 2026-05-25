package com.mezon.classmanagement.backend.domain.auth.service;

import com.mezon.classmanagement.backend.domain.auth.entity.InvalidatedAccessToken;
import com.mezon.classmanagement.backend.domain.auth.repository.InvalidatedAccessTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class InvalidatedAccessTokenService {

	/**
	 * Repository
	 */

	InvalidatedAccessTokenRepository invalidatedAccessTokenRepository;

	@Transactional(readOnly = true)
	public boolean isInvalidated(String jti) {
		return existsByJti(jti);
	}

	/**
	 * Action
	 */

	@Transactional
	public InvalidatedAccessToken save(InvalidatedAccessToken invalidatedAccessToken) {
		return invalidatedAccessTokenRepository.save(invalidatedAccessToken);
	}

	/**
	 * Exists
	 */

	@Transactional(readOnly = true)
	public boolean existsByJti(String jti) {
		return invalidatedAccessTokenRepository.existsByJti(jti);
	}

}