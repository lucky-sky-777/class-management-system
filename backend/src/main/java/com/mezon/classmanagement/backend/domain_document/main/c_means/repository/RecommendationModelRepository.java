package com.mezon.classmanagement.backend.domain_document.main.c_means.repository;

import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationModelRepository
		extends JpaRepository<RecommendationModel, Long> {

	Optional<RecommendationModel>
	findTopByStatusAndTrainedAtIsNotNullOrderByTrainedAtDesc(
			RecommendationModel.Status status
	);

	Optional<RecommendationModel> findTopByOrderByTrainedAtDesc();

	default Optional<RecommendationModel> findLatestReadyModel() {
		return findTopByStatusAndTrainedAtIsNotNullOrderByTrainedAtDesc(
				RecommendationModel.Status.READY
		);
	}
}