package com.mezon.classmanagement.backend.domain.auth.service;

import com.mezon.classmanagement.backend.domain.auth.entity.InvalidatedToken;
import com.mezon.classmanagement.backend.domain.auth.repository.InvalidatedTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class InvalidatedTokenService {

	/**
	 * Repository
	 */

	InvalidatedTokenRepository invalidatedTokenRepository;

	@Transactional(readOnly = true)
	public boolean isInvalidated(String jti) {
		return existsByJti(jti);
	}

	/**
	 * Action
	 */

	@Transactional
	public InvalidatedToken save(InvalidatedToken invalidatedToken) {
		return invalidatedTokenRepository.save(invalidatedToken);
	}

	/**
	 * Exists
	 */

	@Transactional(readOnly = true)
	public boolean existsByJti(String jti) {
		return invalidatedTokenRepository.existsByJti(jti);
	}

}