package com.mezon.classmanagement.backend.domain_document.main.recommendation.repository;

import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.projection.DocumentClusterMembershipProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Document, Long> {

	@Query(value = """
	select
		dcm.document_id,
		rc.cluster_index,
		dcm.membership
	from document_cluster_memberships dcm
	join recommendation_clusters rc
		on dcm.cluster_id = rc.id and rc.model_id = :latestModelId
	where dcm.document_id = :documentId
	order by rc.cluster_index asc
	""", nativeQuery = true)
	List<DocumentClusterMembershipProjection> getDocumentClusterMembership(Long latestModelId, Long documentId);

}