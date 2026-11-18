package com.mezon.classmanagement.backend.domain_document.main.rag.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.env.GeminiEnv;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.main.rag.constant.RagConstant;
import com.mezon.classmanagement.backend.domain_document.main.ai_model.service.chat.GeminiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@SuppressWarnings(value = {WarningConstant.SPELL_CHECKING_INSPECTION})
@Service
public class GeminiRagService {

	private static final String GEMINI_API_URL =
			"https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent?key=";

	private final HttpClient httpClient;
	private final String apiKey;
	private final ObjectMapper objectMapper;
	private final GeminiChatService geminiChatService;

	@Autowired
	public GeminiRagService(GeminiEnv geminiEnv, GeminiChatService geminiChatService) {
		this(
				geminiEnv.apiKey,
				HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(),
				geminiChatService
		);
	}

	public GeminiRagService(
			String apiKey,
			HttpClient httpClient,
			GeminiChatService geminiChatService
	) {
		this.apiKey = apiKey;
		this.httpClient = httpClient;
		this.geminiChatService = geminiChatService;
		this.objectMapper = new ObjectMapper();
	}

	/**
	 * Nhận trực tiếp List<DocumentChunk> để lấy được cả nội dung lẫn Tên tài liệu nguồn
	 */
	public String askGeminiRag(
			String question,
			List<DocumentChunk> chunks
	) throws IOException, InterruptedException {
		// 3. Kỹ thuật XML Tagging + Sandwich Defense
		String userPrompt = String.format(
				RagConstant.USER_PROMPT,
				getContext(chunks),
				sanitizeQuestion(question)
		);

		return geminiChatService.chatRag(userPrompt);

		// 4. Tạo JSON bằng ObjectMapper
//		String requestBodyJson = buildRequestBody(userPrompt);
//
//		HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create(GEMINI_API_URL + this.apiKey))
//				.header("Content-Type", "application/json")
//				.POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
//				.timeout(Duration.ofSeconds(30))
//				.build();
//
//		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//		if (response.statusCode() == 200) {
//			JsonNode rootNode = objectMapper.readTree(response.body());
//			JsonNode candidates = rootNode.path("candidates");
//
//			if (candidates.isArray() && !candidates.isEmpty()) {
//				JsonNode textNode = candidates.get(0)
//						.path("content")
//						.path("parts")
//						.path(0)
//						.path("text");
//				if (!textNode.isMissingNode()) {
//					return textNode.asText().trim();
//				}
//			}
//			return "Tài liệu hiện tại không đề cập đến thông tin này.";
//		} else {
//			throw new RuntimeException("Lỗi gọi Gemini API (HTTP " + response.statusCode() + "): " + response.body());
//		}
	}

	private static String getContext(List<DocumentChunk> chunks) {
		StringBuilder contextBuilder = new StringBuilder();

		if (chunks != null && !chunks.isEmpty()) {
			for (int i = 0; i < chunks.size(); i++) {
				DocumentChunk chunk = chunks.get(i);
				String docTitle =
						(chunk.getDocument() != null && chunk.getDocument().getTitle() != null)
								? chunk.getDocument().getTitle()
								: "Tài liệu nội bộ";

				// Đính kèm tên tài liệu vào từng đoạn để Gemini phân biệt được nguồn khi hỏi cấp Lớp/Hệ thống
				contextBuilder.append(
						String.format(
								"[Đoạn %d | Nguồn: %s]:\n%s\n\n",
								i + 1,
								docTitle,
								chunk.getContent().trim()
						)
				);
			}
		} else {
			contextBuilder.append("(Không có tài liệu / No documents)\n");
		}

		return contextBuilder.toString().trim();
	}

	/**
	 * Làm sạch câu hỏi đầu vào (Tránh việc người dùng tự gõ thẻ đóng </user_query> để thoát khỏi khung)
	 *
	 * @param question Câu hỏi đầu vào
	 * @return Câu hỏi đã được làm sạch
	 */
	private String sanitizeQuestion(String question) {
		return question == null ? "" : question.trim()
				.replace("</user_query>", "")
				.replace("<user_query>", "")
				.replace("</context>", "")
				.replace("<context>", "");
	}

	private String buildRequestBody(String userPrompt) throws IOException {
		ObjectNode root = objectMapper.createObjectNode();

		// System Instruction
		ObjectNode sysNode = root.putObject("system_instruction");
		ArrayNode sysParts = sysNode.putArray("parts");
		sysParts.addObject().put("text", RagConstant.SYSTEM_INSTRUCTION);

		// Contents
		ArrayNode contents = root.putArray("contents");
		ObjectNode contentObj = contents.addObject();
		contentObj.put("role", "user");
		ArrayNode contentParts = contentObj.putArray("parts");
		contentParts.addObject().put("text", userPrompt);

		// Generation Config
		ObjectNode genConfig = root.putObject("generationConfig");
		genConfig.put("temperature", 0.0);
		genConfig.put("topP", 0.8);

		return objectMapper.writeValueAsString(root);
	}
}