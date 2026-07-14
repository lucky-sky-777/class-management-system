package com.mezon.classmanagement.backend_document.domain.component.embedding.dto.request.text;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GeminiBatchTextEmbeddingRequest(
		@JsonProperty(value = "requests")
		List<Item> requests
) {
	public record Item(
			String model,
			GeminiTextEmbeddingRequest.Content content
	) {}
}