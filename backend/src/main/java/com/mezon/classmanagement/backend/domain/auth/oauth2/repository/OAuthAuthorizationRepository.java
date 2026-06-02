package com.mezon.classmanagement.backend.domain.auth.oauth2.repository;

import com.mezon.classmanagement.backend.domain.auth.oauth2.entity.OAuthAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthAuthorizationRepository extends JpaRepository<OAuthAuthorization, String> {
	Optional<OAuthAuthorization> findByCode(String code);
}