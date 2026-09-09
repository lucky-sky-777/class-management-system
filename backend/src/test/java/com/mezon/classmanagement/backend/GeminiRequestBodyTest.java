package com.mezon.classmanagement.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mezon.classmanagement.backend.common.constant.GeminiConstant;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.media.GeminiBatchMediaEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.media.GeminiMediaEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.text.GeminiBatchTextEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.text.GeminiTextEmbeddingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class GeminiRequestBodyTest {

	ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testEmbedSingleMedia() throws Exception {
		GeminiMediaEmbeddingRequest request = new GeminiMediaEmbeddingRequest(
				new GeminiMediaEmbeddingRequest.Content(
						List.of(
								new GeminiMediaEmbeddingRequest.Part(
										new GeminiMediaEmbeddingRequest.InlineData(
												"image/png",
												"base64"
										)
								)
						)
				)
		);

		String json = objectMapper.writeValueAsString(request);

		System.out.println(json);
	}

	@Test
	public void testEmbedMultipleMedia() throws Exception {
		GeminiBatchMediaEmbeddingRequest request = new GeminiBatchMediaEmbeddingRequest(
				List.of(
						new GeminiBatchMediaEmbeddingRequest.Item(
								"models/gemini-embedding-2",
								new GeminiMediaEmbeddingRequest.Content(
										List.of(
												new GeminiMediaEmbeddingRequest.Part(
														new GeminiMediaEmbeddingRequest.InlineData(
																"image/png",
																"base64"
														)
												)
										)
								)
						),
						new GeminiBatchMediaEmbeddingRequest.Item(
								"models/gemini-embedding-2",
								new GeminiMediaEmbeddingRequest.Content(
										List.of(
												new GeminiMediaEmbeddingRequest.Part(
														new GeminiMediaEmbeddingRequest.InlineData(
																"image/png",
																"base64"
														)
												)
										)
								)
						)
				)
		);

		String json = objectMapper.writeValueAsString(request);

		System.out.println(json);
	}

	@Test
	public void testEmbedSingleText() throws Exception {
		GeminiTextEmbeddingRequest request = new GeminiTextEmbeddingRequest(
				GeminiConstant.Model.GEMINI_EMBEDDING_2.getName(),
				new GeminiTextEmbeddingRequest.Content(
						List.of(
								new GeminiTextEmbeddingRequest.Part(
										"text"
								)
						)
				)
		);

		String json = objectMapper.writeValueAsString(request);

		System.out.println(json);
	}

	@Test
	public void testEmbedMultipleText() throws Exception {
		GeminiBatchTextEmbeddingRequest request = new GeminiBatchTextEmbeddingRequest(
				List.of(
						new GeminiBatchTextEmbeddingRequest.Item(
								GeminiConstant.Model.GEMINI_EMBEDDING_2.getName(),
								new GeminiTextEmbeddingRequest.Content(
										List.of(
												new GeminiTextEmbeddingRequest.Part(
														"text"
												)
										)
								)
						),
						new GeminiBatchTextEmbeddingRequest.Item(
								GeminiConstant.Model.GEMINI_EMBEDDING_2.getName(),
								new GeminiTextEmbeddingRequest.Content(
										List.of(
												new GeminiTextEmbeddingRequest.Part(
														"text"
												)
										)
								)
						)
				)
		);

		String json = objectMapper.writeValueAsString(request);

		System.out.println(json);
	}

}