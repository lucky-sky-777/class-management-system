package com.mezon.classmanagement.backend.domain_document.main.c_means.service;

import com.mezon.classmanagement.backend.domain_document.main.c_means.dto.DocumentEmbeddingList;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.DocumentClusterMembership;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationCluster;
import com.mezon.classmanagement.backend.domain_document.main.c_means.repository.DocumentClusterMembershipRepository;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DocumentClusterMembershipService {

	DocumentClusterMembershipRepository documentClusterMembershipRepository;

	private static final int SAVE_BATCH_SIZE = 1000;

	public void create(
			List<RecommendationCluster> savedClusters,
			int clusterCount,
			int documentCount,
			DocumentEmbeddingList documentEmbeddingList,
			double[][] memberships,
			int[] dominantCluster
	) {
		List<DocumentClusterMembership> membershipBatch =
				new ArrayList<>(SAVE_BATCH_SIZE);

		for (int i = 0; i < documentCount; i++) {
			Long documentId = documentEmbeddingList.idList().get(i);

			double[] membershipOfDocument = memberships[i];
			int dominant = dominantCluster[i];

			if (dominant < 0 || dominant >= clusterCount) {
				throw new IllegalStateException(
						"Dominant cluster không hợp lệ cho documentId: "
								+ documentId
				);
			}

			for (int clusterIndex = 0;
			     clusterIndex < clusterCount;
			     clusterIndex++) {

				double membership = membershipOfDocument[clusterIndex];

				if (!Double.isFinite(membership)
						|| membership < 0.0
						|| membership > 1.0) {
					throw new IllegalStateException(
							"Membership không hợp lệ, documentId: "
									+ documentId
					);
				}

				DocumentClusterMembership item =
						DocumentClusterMembership.builder()
								.document(Document.builder().id(documentId).build())
								.cluster(
										RecommendationCluster.builder().id(savedClusters.get(clusterIndex).getId()).build()
								)
								.membership(membership)
								.build();

				membershipBatch.add(item);

				// Lưu theo từng lô để hạn chế bộ nhớ sử dụng
				if (membershipBatch.size() >= SAVE_BATCH_SIZE) {
					documentClusterMembershipRepository
							.saveAll(membershipBatch);

					membershipBatch.clear();
				}
			}
		}

		// Lưu các bản ghi còn lại
		if (!membershipBatch.isEmpty()) {
			documentClusterMembershipRepository.saveAll(membershipBatch);
		}
	}

	@Transactional(readOnly = true)
	public List<DocumentClusterMembership> find10BestDocument(Long clusterId) {
		return documentClusterMembershipRepository
				.findTop10ByCluster_IdOrderByMembershipDesc(clusterId);
	}

	@Transactional(readOnly = true)
	public List<DocumentClusterMembership> find10BestPublicDocument(Long clusterId) {
		return documentClusterMembershipRepository
				.findByClusterAndClassNotPrivate(
						clusterId,
						PageRequest.of(0, 10)
				);
	}

}