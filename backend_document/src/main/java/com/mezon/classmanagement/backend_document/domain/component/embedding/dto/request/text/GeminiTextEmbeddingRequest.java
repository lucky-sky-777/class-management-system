package com.mezon.classmanagement.backend_document.domain.component.embedding.dto.request.text;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GeminiTextEmbeddingRequest(
		@JsonProperty(value = "model")
		String model,

		@JsonProperty(value = "content")
		Content content
) {
	public record Content(
			@JsonProperty(value = "parts")
			List<Part> parts
	) {
	}

	public record Part(
			@JsonProperty(value = "text")
			String text
	) {
	}
}