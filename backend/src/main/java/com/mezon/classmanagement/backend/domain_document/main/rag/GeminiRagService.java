package com.mezon.classmanagement.domain_document.main.rag;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class GeminiRagService {

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    private final HttpClient httpClient;
    private final String apiKey;

    // Constructor mặc định dùng cho Production
    public GeminiRagService(String apiKey) {
        this(apiKey, HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build());
    }

    // Constructor tiêm HttpClient dùng cho Unit Test
    public GeminiRagService(String apiKey, HttpClient httpClient) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
    }

    public String askGeminiRag(String question, List<String> chunks) throws IOException, InterruptedException {
        StringBuilder contextBuilder = new StringBuilder();
        if (chunks != null && !chunks.isEmpty()) {
            for (int i = 0; i < chunks.size(); i++) {
                contextBuilder.append(String.format("[Đoạn %d]: %s\n\n", i + 1, chunks.get(i).trim()));
            }
        } else {
            contextBuilder.append("(Không có ngữ cảnh bổ sung)\n");
        }

        String systemInstruction =
                "Bạn là trợ lý RAG chuyên biệt. Chỉ trả lời dựa trên dữ liệu ngữ cảnh được cấp. " +
                        "Nếu ngữ cảnh không chứa thông tin, hãy thông báo rằng tài liệu không cung cấp thông tin thay vì tự tạo câu trả lời.";

        String userPrompt = String.format(
                "--- NGỮ CẢNH CUNG CẤP ---\n%s\n--- CÂU HỎI ---\n%s\n\nHãy trả lời câu hỏi trên dựa trên ngữ cảnh đã cung cấp.",
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
            return response.body();
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