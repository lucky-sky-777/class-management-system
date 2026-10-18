package com.mezon.classmanagement.backend.domain_document.component.embedding.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.EmbedContentResponse;
import com.mezon.classmanagement.backend.common.constant.GeminiConstant;
import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.env.GeminiEnv;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.common.util.GeminiPromptBuilder;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.media.GeminiBatchMediaEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.media.GeminiMediaEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.text.GeminiBatchTextEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.request.text.GeminiTextEmbeddingRequest;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.response.EmbeddingBatchResponse;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.response.EmbeddingData;
import com.mezon.classmanagement.backend.domain_document.component.embedding.dto.response.EmbeddingResponse;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.BaseVector;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@RequiredArgsConstructor
public class EmbeddingService {

	ObjectMapper objectMapper;
	HttpClient httpClient;

	GeminiEnv geminiEnv;

	AtomicInteger currentApiKeyIndex = new AtomicInteger(0);

	/**
	 * Tạo vector từ một Media (hình ảnh, âm thanh,...).
	 */
	public BaseVector embedSingleMedia(
			byte[] mediaBytes,
			String mimeType
	) throws Exception {
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
				),
				1536
		);

		String jsonBody = objectMapper.writeValueAsString(request);

		return send(jsonBody);
	}

	// Tạo vector từ một Media (hình ảnh, âm thanh,...).
	public BaseVector embedSingleMedia(MultipartFile file) throws Exception {
		byte[] fileBytes = file.getBytes();

		return embedSingleMedia(
				fileBytes,
				FileUtils.detectMimeTypeFromMultipartFile(file)
		);
	}

	// Tạo danh sách vector từ nhiều Media (hình ảnh, âm thanh,...).
	public List<BaseVector> embedMultipleMedia(List<MultipartFile> multipartFileList) throws Exception {
		List<GeminiBatchMediaEmbeddingRequest.Item> itemList = new ArrayList<>();

		for (MultipartFile multipartFile : multipartFileList) {
			itemList.add(
					new GeminiBatchMediaEmbeddingRequest.Item(
							"models/gemini-embedding-2",
							new GeminiMediaEmbeddingRequest.Content(
									List.of(
											new GeminiMediaEmbeddingRequest.Part(
													new GeminiMediaEmbeddingRequest.InlineData(
															FileUtils.detectMimeTypeFromMultipartFile(multipartFile),
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

	// Tạo vector từ một đoạn văn bản.
	// Dùng khi văn bản là một truy vấn (nội dung tìm kiếm, đặt câu hỏi,...).
	public BaseVector embedSingleQueryText(String content) {
		String prompt = GeminiPromptBuilder.buildQueryPrompt(content);

		try {
			return embedSingleText(prompt);
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR,
					e.getMessage()
			);
		}
	}

	// Tạo vector từ một đoạn văn bản.
	// Dùng khi văn bản là một thông tin (nội dung trong tài liệu, câu trả lời,...).
	public BaseVector embedSingleDocumentText(
			String title,
			String content
	) {
		String prompt = GeminiPromptBuilder.buildDocumentPrompt(title, content);

		try {
			return embedSingleText(prompt);
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR,
					e.getMessage()
			);
		}
	}

	private BaseVector embedSingleText(String prompt) throws Exception {
		GeminiTextEmbeddingRequest request = new GeminiTextEmbeddingRequest(
				GeminiConstant.Model.GEMINI_EMBEDDING_2.getName(),
				new GeminiTextEmbeddingRequest.Content(
						List.of(
								new GeminiTextEmbeddingRequest.Part(
										prompt
								)
						)
				),
				1536
		);

		String jsonBody = objectMapper.writeValueAsString(request);

		return send(jsonBody);
	}

	public List<BaseVector> embedMultipleQueryText(List<String> contentList) throws Exception {
		List<String> promptList = new ArrayList<>();

		for (String content : contentList) {
			promptList.add(
					GeminiPromptBuilder.buildQueryPrompt(content)
			);
		}

		return embedMultipleText(promptList);
	}

	public List<BaseVector> embedMultipleDocumentText(
			String title,
			List<String> chunkList
	) {
		List<String> promptList = new ArrayList<>();

		for (String chunk : chunkList) {
			promptList.add(
					GeminiPromptBuilder.buildDocumentPrompt(
							title,
							chunk
					)
			);
		}

		try {
			return embedMultipleText(promptList);
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR,
					e.getMessage()
			);
		}
	}

	private List<BaseVector> embedMultipleText(List<String> promptList) throws Exception {

		List<BaseVector> allVectors = new ArrayList<>();

		int totalChunks = promptList.size();

		for (int i = 0; i < totalChunks; i += MAX_BATCH_SIZE) {
			int endIndex = Math.min(i + MAX_BATCH_SIZE, totalChunks);
			List<String> subList = promptList.subList(i, endIndex);

			List<GeminiBatchTextEmbeddingRequest.Item> itemList = it(subList);

			GeminiBatchTextEmbeddingRequest batchRequestBody = new GeminiBatchTextEmbeddingRequest(itemList);
			String jsonBody = objectMapper.writeValueAsString(batchRequestBody);

			List<BaseVector> batchResult = sendBatch(jsonBody);

			allVectors.addAll(batchResult);

			if (endIndex < totalChunks) {
				Thread.sleep(5000);
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
							),
							1536
					)
			);
		}

		return individualRequests;
	}

	private float[] convertListToArray(List<Float> floatList) {
		float[] floatArray = new float[floatList.size()];

		for (int i = 0; i < floatList.size(); i++) {
			floatArray[i] = floatList.get(i);
		}

		return floatArray;
	}

	private static final int MAX_BATCH_SIZE = 10;
	//private static final String MODEL_NAME = "models/gemini-embedding-2";


	private String nextApiKey() {
		List<String> apiKeys = geminiEnv.apiKeyList;

		if (apiKeys == null || apiKeys.isEmpty()) {
			throw new IllegalStateException(
					"GEMINI_API_KEY_LIST không được để trống"
			);
		}

		int index = currentApiKeyIndex.getAndUpdate(
				current -> (current + 1) % apiKeys.size()
		);

		return apiKeys.get(index);
	}

	private void status429() {
		throw new GlobalException(
				GlobalException.Type.TOO_MANY_REQUEST,
				"Too many request"
		);
	}

	private BaseVector send(String json) throws Exception {
		String key = nextApiKey();
		System.out.println(key);

		HttpRequest request = buildRequest(
				geminiEnv.apiUrl,
				key,
				json
		);

		HttpResponse<String> response = getResponse(request);

		if (response.statusCode() == 200) {
			EmbeddingResponse embedResponse = objectMapper.readValue(response.body(), EmbeddingResponse.class);
			return new BaseVector(convertListToArray(embedResponse.embedding().values()));
		}
		if (response.statusCode() == 429) {
			status429();
		}

		throw new RuntimeException(response.body());
	}

	private List<BaseVector> sendBatch(String json) throws Exception {
		String key = nextApiKey();
		System.out.println(key);

		HttpRequest request = buildRequest(
				geminiEnv.batchApiUrl,
				key,
				json
		);

		HttpResponse<String> response = getResponse(request);

		if (response.statusCode() == 200) {
			EmbeddingBatchResponse batchResponse = objectMapper.readValue(response.body(), EmbeddingBatchResponse.class);
			List<BaseVector> vectorList = new ArrayList<>();
			for (EmbeddingData embeddingData : batchResponse.embeddings()) {
				vectorList.add(new BaseVector(convertListToArray(embeddingData.values())));
			}
			return vectorList;
		}
		if (response.statusCode() == 429) {
			status429();
		}

		throw new RuntimeException(response.body());
	}

	private HttpRequest buildRequest(
			String url,
			String apiKey,
			String json
	) {
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