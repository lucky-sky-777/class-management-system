package com.mezon.classmanagement.backend.domain_ai.dto.media;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GeminiBatchMediaEmbeddingRequest(
		@JsonProperty(value = "requests")
		List<Item> requests
) {
	public record Item(
			String model,
			GeminiMediaEmbeddingRequest.Content content
	) {}
}