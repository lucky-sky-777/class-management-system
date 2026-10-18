package com.mezon.classmanagement.backend.domain_document.main.rag.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mezon.classmanagement.backend.common.env.GeminiEnv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Service
public class GeminiRagService {

	private static final String GEMINI_API_URL =
			"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

	private final HttpClient httpClient;
	private final String apiKey;

	@Autowired
	public GeminiRagService(GeminiEnv geminiEnv) {
		this(geminiEnv.apiKey, HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build());
	}

	public GeminiRagService(
			String apiKey,
			HttpClient httpClient
	) {
		this.apiKey = apiKey;
		this.httpClient = httpClient;
	}

	public String askGeminiRag(
			String question,
			List<String> chunks
	) throws IOException, InterruptedException {
		StringBuilder contextBuilder = new StringBuilder();
		if (chunks != null && !chunks.isEmpty()) {
			for (int i = 0; i < chunks.size(); i++) {
				contextBuilder.append(String.format("[Đoạn %d]: %s\n\n", i + 1, chunks.get(i).trim()));
			}
		} else {
			contextBuilder.append("(Không có ngữ cảnh bổ sung)\n");
		}

		String systemInstruction =
				"Bạn là một trợ lý ảo chuyên nghiệp. Nhiệm vụ của bạn là trả lời câu hỏi trực tiếp, tự nhiên và gãy gọn dựa HOÀN TOÀN vào tài liệu được cung cấp. " +
						"Tuyệt đối KHÔNG sử dụng các câu mào đầu như 'Dựa trên ngữ cảnh được cung cấp' hay 'Theo tài liệu'. Hãy vào thẳng câu trả lời. " +
						"Nếu tài liệu không chứa thông tin để trả lời, chỉ cần đáp ngắn gọn: 'Tài liệu hiện tại không đề cập đến thông tin này.'";

		String userPrompt = String.format(
				"TÀI LIỆU:\n%s\n\nCÂU HỎI:\n%s\n\nTRẢ LỜI:",
				contextBuilder.toString().trim(),
				question.trim()
		);

		String requestBodyJson = String.format(
				"""
				{
				  "system_instruction": {
					"parts": [{"text": "%s"}]
				  },
				  "contents": [
					{
					  "parts": [{"text": "%s"}]
					}
				  ],
				  "generationConfig": {
					"temperature": 0.2,
					"topP": 0.8
				  }
				}
				""",
				escapeJson(systemInstruction),
				escapeJson(userPrompt)
		);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(GEMINI_API_URL + this.apiKey))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
				.timeout(Duration.ofSeconds(30))
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() == 200) {
			// Dùng ObjectMapper của Spring Boot để đọc JSON
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(response.body());

			// Trích xuất chính xác đường dẫn: candidates[0].content.parts[0].text
			String answerText = rootNode.path("candidates").get(0)
					.path("content")
					.path("parts").get(0)
					.path("text").asText();

			return answerText;
		} else {
			throw new RuntimeException("Lỗi gọi Gemini API (HTTP " + response.statusCode() + "): " + response.body());
		}
	}

	private String escapeJson(String input) {
		if (input == null) return "";
		return input.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\b", "\\b")
				.replace("\f", "\\f")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
	}

}