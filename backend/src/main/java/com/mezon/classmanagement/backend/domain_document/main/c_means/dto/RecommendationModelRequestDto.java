package com.mezon.classmanagement.backend.domain_document.main.c_means.dto;

import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationModel;

public record RecommendationModelRequestDto(
		int clusterCount,
		RecommendationModel.Status status
) {
}