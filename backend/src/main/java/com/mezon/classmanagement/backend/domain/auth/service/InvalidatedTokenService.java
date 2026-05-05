package com.mezon.classmanagement.backend.domain.auth.service;

import com.mezon.classmanagement.backend.domain.auth.repository.InvalidatedTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class InvalidatedTokenService {

	/**
	 * Repository
	 */

	InvalidatedTokenRepository invalidatedTokenRepository;

	public boolean isInvalidated(String jti) {
		return invalidatedTokenRepository.existsByJti(jti);
	}

}