package com.mezon.classmanagement.backend.domain.auth.repository;

import com.mezon.classmanagement.backend.domain.auth.entity.InvalidatedAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedAccessTokenRepository extends JpaRepository<InvalidatedAccessToken, Long> {

	boolean existsByJti(String jti);

}