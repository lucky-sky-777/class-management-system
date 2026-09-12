package com.mezon.classmanagement.backend.domain.auth.service;

import com.mezon.classmanagement.backend.common.constant.ForgotPasswordConstant;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.CodeGenerator;
import com.mezon.classmanagement.backend.common.util.DateTimeUtils;
import com.mezon.classmanagement.backend.domain.auth.entity.PasswordResetToken;
import com.mezon.classmanagement.backend.domain.auth.repository.PasswordResetTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class PasswordResetTokenService {

    PasswordResetTokenRepository passwordResetTokenRepository;
    PasswordEncoder passwordEncoder;
    ForgotPasswordConstant forgotPasswordConstant;

    @Transactional
    public String createOtp(Long userId) {
        // Vô hiệu hoá mã cũ chưa dùng (nếu có) để tránh tồn tại nhiều mã hợp lệ cùng lúc
        passwordResetTokenRepository
                .findFirstByUserIdAndUsedFalseOrderByIdDesc(userId)
                .ifPresent(old -> {
                    old.setUsed(true);
                    passwordResetTokenRepository.save(old);
                });

        String rawCode = CodeGenerator.generate(forgotPasswordConstant.OTP_LENGTH);

        PasswordResetToken token = PasswordResetToken.builder()
                .userId(userId)
                .codeHash(passwordEncoder.encode(rawCode))
                .expiryDate(
                        Instant.ofEpochMilli(
                                DateTimeUtils.minutesInTimestamp(forgotPasswordConstant.OTP_EXPIRY_MINUTES)
                        )
                )
                .build();

        passwordResetTokenRepository.save(token);

        return rawCode;
    }

    @Transactional
    public void verifyAndConsume(Long userId, String rawCode) {
        PasswordResetToken token = passwordResetTokenRepository
                .findFirstByUserIdAndUsedFalseOrderByIdDesc(userId)
                .orElseThrow(() ->
                        new GlobalException(GlobalException.Type.INVALID_REQUEST, "Mã xác nhận không hợp lệ")
                );

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Mã xác nhận đã hết hạn");
        }

        if (token.getAttempts() >= forgotPasswordConstant.MAX_ATTEMPTS) {
            throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Bạn đã nhập sai quá số lần cho phép, vui lòng yêu cầu mã mới");
        }

        if (!passwordEncoder.matches(rawCode, token.getCodeHash())) {
            token.setAttempts(token.getAttempts() + 1);
            passwordResetTokenRepository.save(token);
            throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Mã xác nhận không đúng");
        }

        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }

}