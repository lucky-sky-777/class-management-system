package com.mezon.classmanagement.backend.domain.auth.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain.auth.entity.RefreshToken;
import com.mezon.classmanagement.backend.domain.auth.repository.RefreshTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    /**
     * Repository
     */

    RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void revokeByJti(String jti) {
        RefreshToken currentRefreshToken = findByJtiOrThrow(jti);
        currentRefreshToken.setRevoked(true);
        save(currentRefreshToken);
    }

    /**
     * Action
     */

    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Find
     */

    @Transactional(readOnly = true)
    public RefreshToken findByJtiOrThrow(String jti) {
        return refreshTokenRepository
                .findByJti(jti)
                .orElseThrow(() ->
                        new GlobalException(GlobalException.Type.NOT_FOUND, "Refresh token not found")
                );
    }

    /**
     * Validate
     */

    @Transactional(readOnly = true)
    public boolean isRevoked(String jti) {
        return refreshTokenRepository
                .findByJti(jti)
                .map(RefreshToken::getRevoked)
                .orElse(true);
    }

    @Transactional(readOnly = true)
    public void throwIfIsRevoked(String jti) {
        if (isRevoked(jti)) {
            throw new GlobalException(GlobalException.Type.INVALID_AUTHENTICATION, "Refresh token revoked");
        }
    }

}