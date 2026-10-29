package com.mezon.classmanagement.backend.domain_document.main.c_means.repository;

import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationCluster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.DocumentClusterMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentClusterMembershipRepository
		extends JpaRepository<DocumentClusterMembership, Long> {

	List<DocumentClusterMembership> findAllByCluster_IdOrderByMembershipDesc(Long clusterId);
	List<DocumentClusterMembership> findTop10ByCluster_IdOrderByMembershipDesc(Long clusterId);

	List<DocumentClusterMembership>
	findAllByDocument_Id(Long documentId);
}