package com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.media;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GeminiMediaEmbeddingRequest(
		@JsonProperty(value = "content")
		Content content
) {
	public record Content(
			@JsonProperty(value = "parts")
			List<Part> parts
	) {
	}

	public record Part(
			@JsonProperty(value = "inline_data")
			InlineData inlineData
	) {
	}

	public record InlineData(
			@JsonProperty(value = "mime_type")
			String mimeType,

			@JsonProperty(value = "data")
			String data
	) {
	}
}