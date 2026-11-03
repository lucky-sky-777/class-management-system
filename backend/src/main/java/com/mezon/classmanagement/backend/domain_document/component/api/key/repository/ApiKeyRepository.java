package com.mezon.classmanagement.backend.domain_document.component.api.key.repository;

import com.mezon.classmanagement.backend.domain_document.component.api.key.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

	List<ApiKey> findAllByProvider(ApiKey.Provider provider);

}