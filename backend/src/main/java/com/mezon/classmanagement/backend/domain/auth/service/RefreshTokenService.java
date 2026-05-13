package com.mezon.classmanagement.backend.domain.auth.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain.auth.entity.RefreshToken;
import com.mezon.classmanagement.backend.domain.auth.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken save(RefreshToken refreshToken){
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken findByJtiOrThrow(String jti) {
        return refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new GlobalException(GlobalException.Type.NOT_FOUND, "Refresh token not found"));
    }

    @Transactional
    public void revokeByJti(String jti) {
        RefreshToken token = findByJtiOrThrow(jti);
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Transactional
    public boolean isRevoked(String jti) {
        return refreshTokenRepository.findByJti(jti)
                .map(RefreshToken::isRevoked)
                .orElse(true); // không tìm thấy → coi như revoked
    }
}
