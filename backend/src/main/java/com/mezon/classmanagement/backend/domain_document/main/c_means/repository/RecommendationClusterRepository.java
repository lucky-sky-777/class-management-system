package com.mezon.classmanagement.backend.domain_document.main.c_means.repository;

import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationCluster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationClusterRepository
		extends JpaRepository<RecommendationCluster, Long> {

	List<RecommendationCluster>
	findAllByModel_IdOrderByClusterIndexAsc(Long modelId);
}