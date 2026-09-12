package com.mezon.classmanagement.backend.domain.auth.repository;

import com.mezon.classmanagement.backend.domain.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findFirstByUserIdAndUsedFalseOrderByIdDesc(Long userId);

}