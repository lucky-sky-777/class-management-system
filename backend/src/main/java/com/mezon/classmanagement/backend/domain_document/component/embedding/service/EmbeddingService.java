package com.mezon.classmanagement.backend.domain_document.component.embedding.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mezon.classmanagement.backend.common.constant.GeminiConstant;
import com.mezon.classmanagement.backend.common.env.GeminiEnv;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.common.util.GeminiPromptBuilder;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.media.GeminiBatchMediaEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.media.GeminiMediaEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.text.GeminiBatchTextEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.text.GeminiTextEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.response.EmbeddingBatchResponse;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.response.EmbeddingData;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.response.EmbeddingResponse;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.Vector;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector3072;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
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

	GeminiEnv geminiEnv;

	public void test() {
	}

	public String toVectorString(Vector3072 vector) {
		return vector.toString();
	}

	public String toVectorString(Vector1536 vector) {
		return vector.toString();
	}

	public float[] convertListToArray(List<Float> floatList) {
		float[] floatArray = new float[floatList.size()];

		for (int i = 0; i < floatList.size(); i++) {
			floatArray[i] = floatList.get(i);
		}

		return floatArray;
	}

	public Vector1536 embedSingleMedia(byte[] mediaBytes, String mimeType) throws Exception {
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

	public Vector1536 embedSingleMedia(MultipartFile file) throws Exception {
		byte[] fileBytes = file.getBytes();

		return embedSingleMedia(
				fileBytes,
				FileUtils.getMimeType(fileBytes)
		);
	}

	public List<Vector3072> embedMultipleMedia(List<MultipartFile> multipartFileList) throws Exception {
		List<GeminiBatchMediaEmbeddingRequest.Item> itemList = new ArrayList<>();

		for (MultipartFile multipartFile : multipartFileList) {
			itemList.add(
					new GeminiBatchMediaEmbeddingRequest.Item(
							"models/gemini-embedding-2",
							new GeminiMediaEmbeddingRequest.Content(
									List.of(
											new GeminiMediaEmbeddingRequest.Part(
													new GeminiMediaEmbeddingRequest.InlineData(
															FileUtils.getMimeType(multipartFile.getBytes()),
															"base64"
													)
											)
									)
							)
					)
			);
		}

		GeminiBatchMediaEmbeddingRequest request = new GeminiBatchMediaEmbeddingRequest(
				itemList
		);

		String json = objectMapper.writeValueAsString(request);

		return sendBatch(json);
	}

	/**
	 * Text
	 */

	public Vector1536 embedSingleQueryText(String content) throws Exception {
		String prompt = GeminiPromptBuilder.buildQueryPrompt(content);

		return embedSingleText(prompt);
	}

	public Vector1536 embedSingleDocumentText(String title, String content) throws Exception {
		String prompt = GeminiPromptBuilder.buildDocumentPrompt(title, content);

		return embedSingleText(prompt);
	}

	private Vector1536 embedSingleText(String prompt) throws Exception {
		GeminiTextEmbeddingRequest request = new GeminiTextEmbeddingRequest(
				GeminiConstant.Model.GEMINI_EMBEDDING_2.getName(),
				new GeminiTextEmbeddingRequest.Content(
						List.of(
								new GeminiTextEmbeddingRequest.Part(
										prompt
								)
						)
				)
		);

		String jsonBody = objectMapper.writeValueAsString(request);

		return send(jsonBody);
	}

	public List<Vector3072> embedMultipleQueryText(List<String> contentList) throws Exception {
		List<String> promptList = new ArrayList<>();

		for (String content : contentList) {
			promptList.add(
					GeminiPromptBuilder.buildQueryPrompt(content)
			);
		}

		return embedMultipleText(promptList);
	}

	public List<Vector3072> embedMultipleDocumentText(List<Pair<String, String>> titleAndContentList) throws Exception {
		List<String> promptList = new ArrayList<>();

		for (Pair<String, String> titleAndContent : titleAndContentList) {
			promptList.add(
					GeminiPromptBuilder.buildDocumentPrompt(
							titleAndContent.getFirst(),
							titleAndContent.getSecond()
					)
			);
		}

		return embedMultipleText(promptList);
	}

	private List<Vector3072> embedMultipleText(List<String> promptList) throws Exception {

		List<Vector3072> allVectors = new ArrayList<>();

		int totalChunks = promptList.size();

		for (int i = 0; i < totalChunks; i += MAX_BATCH_SIZE) {
			int endIndex = Math.min(i + MAX_BATCH_SIZE, totalChunks);
			List<String> subList = promptList.subList(i, endIndex);

			List<GeminiBatchTextEmbeddingRequest.Item> itemList = it(subList);

			GeminiBatchTextEmbeddingRequest batchRequestBody = new GeminiBatchTextEmbeddingRequest(itemList);
			String jsonBody = objectMapper.writeValueAsString(batchRequestBody);

			List<Vector3072> batchResult = sendBatch(jsonBody);

			allVectors.addAll(batchResult);

			if (endIndex < totalChunks) {
				Thread.sleep(500);
			}
		}

		return allVectors;
	}

	private List<GeminiBatchTextEmbeddingRequest.Item> it(List<String> subList) {
		List<GeminiBatchTextEmbeddingRequest.Item> individualRequests = new ArrayList<>();

		for (String chunkText : subList) {
			individualRequests.add(
					new GeminiBatchTextEmbeddingRequest.Item(
							GeminiConstant.Model.GEMINI_EMBEDDING_2.getName(),
							new GeminiTextEmbeddingRequest.Content(
									List.of(
											new GeminiTextEmbeddingRequest.Part(
													chunkText
											)
									)
							)
					)
			);
		}

		return individualRequests;
	}

//	public List<ChunkEmbeddingResult> processDocument(String fullText, int chunkSize, int chunkOverlap) throws Exception {
//		List<ChunkEmbeddingResult> results = new ArrayList<>();
//
//		List<String> chunks = TextSplitter.splitText(fullText, chunkSize, chunkOverlap);
//
//		for (String chunk : chunks) {
//			Vector vector = embedText(chunk);
//			results.add(new ChunkEmbeddingResult(chunk, vector));
//		}
//
//		return results;
//	}
//
//	public record ChunkEmbeddingResult(String textChunk, Vector vector) {}
//
	private static final int MAX_BATCH_SIZE = 100;
//	private static final String MODEL_NAME = "models/gemini-embedding-2";


	private Vector1536 send(String json) throws Exception {
		HttpRequest request = buildRequest(
				geminiEnv.apiUrl,
				geminiEnv.apiKey,
				json
		);

		HttpResponse<String> response = getResponse(request);

		if (response.statusCode() == 200) {
			EmbeddingResponse embedResponse = objectMapper.readValue(response.body(), EmbeddingResponse.class);
			return new Vector1536(convertListToArray(embedResponse.embedding().values()));
		}

		throw new RuntimeException(response.body());
	}

	private List<Vector3072> sendBatch(String json) throws Exception {
		HttpRequest request = buildRequest(
				geminiEnv.batchApiUrl,
				geminiEnv.apiKey,
				json
		);

		HttpResponse<String> response = getResponse(request);

		if (response.statusCode() == 200) {
			EmbeddingBatchResponse batchResponse = objectMapper.readValue(response.body(), EmbeddingBatchResponse.class);
			List<Vector3072> vectorList = new ArrayList<>();
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