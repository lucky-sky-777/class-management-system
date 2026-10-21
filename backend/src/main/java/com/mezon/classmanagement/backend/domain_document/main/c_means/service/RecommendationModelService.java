package com.mezon.classmanagement.backend.domain_document.main.c_means.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain_document.main.c_means.dto.RecommendationModelRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationModel;
import com.mezon.classmanagement.backend.domain_document.main.c_means.repository.RecommendationModelRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class RecommendationModelService {

	RecommendationModelRepository recommendationModelRepository;

	@Transactional
	public RecommendationModel create(RecommendationModelRequestDto request) {
		return save(
				RecommendationModel
						.builder()
						.clusterCount(request.clusterCount())
						.status(request.status())
						.build()
		);
	}

	@Transactional
	public RecommendationModel update(Long id, RecommendationModel.Status status) {
		RecommendationModel recommendationModel = findByIdOrThrow(id);
		recommendationModel.setStatus(status);
		recommendationModel.setTrainedAt(Instant.now());

		return save(recommendationModel);
	}

	@Transactional
	public RecommendationModel save(RecommendationModel recommendationModel) {
		return recommendationModelRepository.save(recommendationModel);
	}

	@Transactional
	public RecommendationModel findByIdOrThrow(Long id) {
		return recommendationModelRepository
				.findById(id)
				.orElseThrow(() ->
						new GlobalException(
								GlobalException.Type.NOT_EXISTS,
								"RecommedationModel not exists"
						)
				);
	}

}