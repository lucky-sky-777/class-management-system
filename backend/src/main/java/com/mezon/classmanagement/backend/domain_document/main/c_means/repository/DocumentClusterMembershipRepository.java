package com.mezon.classmanagement.backend.domain_document.main.c_means.repository;

import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationCluster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.DocumentClusterMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentClusterMembershipRepository
		extends JpaRepository<DocumentClusterMembership, Long> {

	List<DocumentClusterMembership>
	findAllByClusterIdOrderByMembershipDesc(Long clusterId);

	List<DocumentClusterMembership>
	findAllByDocumentId(Long documentId);
}