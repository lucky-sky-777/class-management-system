package com.mezon.classmanagement.backend.domain_document.main.document.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.lang.annotation.Inherited;

public interface RecommendationProjection extends DocumentResponseDtoProjection {

	@JsonProperty("cosine_distance")
	Double getCosineDistance();

	@JsonProperty("cosine_similarity")
	Double getCosineSimilarity();

}