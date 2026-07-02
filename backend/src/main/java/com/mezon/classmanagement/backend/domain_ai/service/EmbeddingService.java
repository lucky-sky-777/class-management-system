package com.mezon.classmanagement.backend.domain_ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mezon.classmanagement.backend.domain_ai.constant.GeminiConstant;
import com.mezon.classmanagement.backend.domain_ai.dto.EmbeddingData;
import com.mezon.classmanagement.backend.domain_ai.dto.GeminiEmbeddingResponse;
import com.mezon.classmanagement.backend.domain_ai.dto.batch.GeminiBatchRequest;
import com.mezon.classmanagement.backend.domain_ai.dto.batch.GeminiBatchResponse;
import com.mezon.classmanagement.backend.domain_ai.dto.media.GeminiMediaEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_ai.dto.text.EmbedIndividualRequest;
import com.mezon.classmanagement.backend.domain_ai.dto.text.GeminiTextEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_ai.dto.text.TextContent;
import com.mezon.classmanagement.backend.domain_ai.dto.text.TextPart;
import com.mezon.classmanagement.backend.domain_ai.entity.Vector;
import com.mezon.classmanagement.backend.domain_ai.entity.Vector3072;
import com.mezon.classmanagement.backend.domain_ai.util.TextSplitter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
//import org.springframework.ai.vertexai.embedding.multimodal.VertexAiMultimodalEmbeddingOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@RequiredArgsConstructor
public class EmbeddingService {

	ObjectMapper objectMapper;
	HttpClient httpClient;

	public String toVectorString(Vector3072 vector) {
		return vector.toString();
	}

	public float[] convertListToArray(List<Float> floatList) {
		float[] floatArray = new float[floatList.size()];

		for (int i = 0; i < floatList.size(); i++) {
			floatArray[i] = floatList.get(i);
		}

		return floatArray;
	}

	GeminiConstant geminiConstant;

	public Vector embedSingleMedia(byte[] mediaBytes, String mimeType) throws Exception {
		String base64Data = Base64.getEncoder().encodeToString(mediaBytes);

		GeminiMediaEmbeddingRequest request = new GeminiMediaEmbeddingRequest(
				new GeminiMediaEmbeddingRequest.Content(
						List.of(
								new GeminiMediaEmbeddingRequest.Part(
										new GeminiMediaEmbeddingRequest.InlineData(
												mimeType,
												base64Data
										)
								)
						)
				)
		);

		String jsonBody = objectMapper.writeValueAsString(request);

		return send(jsonBody);
	}

	public Vector embedMultipleMedia(List<MultipartFile> multipartFileList) throws Exception {
		return null;
	}

	public Vector embedSingleMedia(MultipartFile file) throws Exception {
		return embedSingleMedia(
				file.getBytes(),
				file.getContentType()
		);
	}

	private Vector embedText(String text) throws Exception {
		TextPart part = new TextPart(text);
		TextContent content = new TextContent(List.of(part));
		GeminiTextEmbeddingRequest requestBody = new GeminiTextEmbeddingRequest(content);

		String jsonBody = objectMapper.writeValueAsString(requestBody);

		return send(jsonBody);
	}

	public List<ChunkEmbeddingResult> processDocument(String fullText, int chunkSize, int chunkOverlap) throws Exception {
		List<ChunkEmbeddingResult> results = new ArrayList<>();

		List<String> chunks = TextSplitter.splitText(fullText, chunkSize, chunkOverlap);

		for (String chunk : chunks) {
			Vector vector = embedText(chunk);
			results.add(new ChunkEmbeddingResult(chunk, vector));
		}

		return results;
	}

	public record ChunkEmbeddingResult(String textChunk, Vector vector) {}

	private static final int MAX_BATCH_SIZE = 100;
	private static final String MODEL_NAME = "models/gemini-embedding-2";

	public List<Vector> generateBatchEmbeddings(List<String> allChunks) throws Exception {
		List<Vector> allVectors = new ArrayList<>();

		int totalChunks = allChunks.size();

		for (int i = 0; i < totalChunks; i += MAX_BATCH_SIZE) {
			int endIndex = Math.min(i + MAX_BATCH_SIZE, totalChunks);
			List<String> subList = allChunks.subList(i, endIndex);

			List<EmbedIndividualRequest> individualRequests = new ArrayList<>();
			for (String chunkText : subList) {
				TextPart part = new TextPart(chunkText);
				TextContent content = new TextContent(List.of(part));
				individualRequests.add(new EmbedIndividualRequest(MODEL_NAME, content));
			}

			GeminiBatchRequest batchRequestBody = new GeminiBatchRequest(individualRequests);
			String jsonBody = objectMapper.writeValueAsString(batchRequestBody);

			List<Vector> batchResult = sendBatch(jsonBody);

			allVectors.addAll(batchResult);

			if (endIndex < totalChunks) {
				Thread.sleep(500);
			}
		}

		return allVectors;
	}

	private Vector send(String json) throws Exception {
		HttpRequest request = buildRequest(
				geminiConstant.apiUrl,
				geminiConstant.apiKey,
				json
		);

		HttpResponse<String> response = getResponse(request);

		if (response.statusCode() == 200) {
			GeminiEmbeddingResponse embedResponse = objectMapper.readValue(response.body(), GeminiEmbeddingResponse.class);
			return new Vector3072(convertListToArray(embedResponse.embeddingData().values()));
		}

		throw new RuntimeException(response.body());
	}

	private List<Vector> sendBatch(String json) throws Exception {
		HttpRequest request = buildRequest(
				geminiConstant.batchApiUrl,
				geminiConstant.apiKey,
				json
		);

		HttpResponse<String> response = getResponse(request);

		if (response.statusCode() == 200) {
			GeminiBatchResponse batchResponse = objectMapper.readValue(response.body(), GeminiBatchResponse.class);
			List<Vector> vectorList = new ArrayList<>();
			for (EmbeddingData embeddingData : batchResponse.embeddings()) {
				vectorList.add(new Vector3072(convertListToArray(embeddingData.values())));
			}
			return vectorList;
		}

		throw new RuntimeException(response.body());
	}

	private HttpRequest buildRequest(String url, String apiKey, String json) {
		return HttpRequest.newBuilder()
				.uri(URI.create(url + "?key=" + apiKey))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.build();
	}

	private HttpResponse<String> getResponse(HttpRequest request) throws Exception {
		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

}