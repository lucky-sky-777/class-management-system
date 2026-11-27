package com.mezon.classmanagement.backend.domain_document.main.recommendation.evaluator;

import com.mezon.classmanagement.backend.domain_document.component.user_vector.UserVectorService;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationModel;
import com.mezon.classmanagement.backend.domain_document.main.c_means.repository.DocumentClusterMembershipRepository;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.DocumentClusterMembershipService;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.RecommendationModelService;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.calculator.dto.ClusterPreference;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.projection.DocumentClusterMembershipProjection;

import java.util.List;
import java.util.Optional;

public class EvaluatorService {


	RecommendationModelService recommendationModelService;
	UserVectorService userVectorService;
	DocumentClusterMembershipService documentClusterMembershipService;

	public double evaluate(
			Long documentId,
			Long userId
	) {
		RecommendationModel latestModel = recommendationModelService
				.findLatestReadyModelOrThrow();

		Optional<List<ClusterPreference>> o1 = userVectorService.getPreferencePerCluster(
				userId,
				latestModel.getId()
		);

		Optional<List<DocumentClusterMembershipProjection>> o2 = getDocumentPreferencePerCluster(
				documentId,
				latestModel.getId()
		);

		if (o1.isPresent() && o2.isPresent()) {
			return calculateClusterSimilarity(o1.get(), o2.get());
		}

		return 0.0d;
	}

	private Optional<List<DocumentClusterMembershipProjection>> getDocumentPreferencePerCluster(
			Long documentId,
			Long latestModelId
	) {
		List<DocumentClusterMembershipProjection> r = documentClusterMembershipService
				.getMembershipOfDocumentWithEachCluster(latestModelId, documentId);

		if (r.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(r);
		}
	}

	private double calculateClusterSimilarity(
			List<ClusterPreference> userClusterPreferences,
			List<DocumentClusterMembershipProjection> documentClusterMemberships
	) {
		if (userClusterPreferences == null || documentClusterMemberships == null) {
			throw new IllegalArgumentException("Vectors must not be null");
		}

		if (userClusterPreferences.size() != documentClusterMemberships.size()) {
			throw new IllegalArgumentException("Vectors must have the same dimension");
		}

		double similarity = 0.0;

		for (int i = 0; i < userClusterPreferences.size(); i++) {
			similarity += userClusterPreferences.get(i).preference() * documentClusterMemberships.get(i).getMembership();
		}

		return similarity;
	}

}