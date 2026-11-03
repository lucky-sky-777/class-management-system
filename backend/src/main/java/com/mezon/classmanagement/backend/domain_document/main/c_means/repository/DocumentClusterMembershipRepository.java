package com.mezon.classmanagement.backend.domain_document.main.c_means.repository;

import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationCluster;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.DocumentClusterMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentClusterMembershipRepository
		extends JpaRepository<DocumentClusterMembership, Long> {

	List<DocumentClusterMembership> findAllByCluster_IdOrderByMembershipDesc(Long clusterId);
	List<DocumentClusterMembership> findTop10ByCluster_IdOrderByMembershipDesc(Long clusterId);

	List<DocumentClusterMembership>
	findAllByDocument_Id(Long documentId);

	@Query("""
    select dcm
    from DocumentClusterMembership dcm
    join dcm.document document
    where dcm.cluster.id = :clusterId
        and exists (
            select dd.id
            from DirectoryDocument dd
            join dd.directory directory
            where dd.document.id = document.id
                and directory.clazz.privacy = 'PUBLIC'
        )
    order by dcm.membership desc
    """)
	List<DocumentClusterMembership> findByClusterAndClassNotPrivate(
			@Param("clusterId") Long clusterId,
			Pageable pageable
	);
}