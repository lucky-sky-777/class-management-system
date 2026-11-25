
package com.mezon.classmanagement.backend.domain_document.main.rag.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mezon.classmanagement.backend.common.env.GeminiEnv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Service
public class HydeService {

    private static final String GEMINI_HYDE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + "gemini-3.5-flash-lite:generateContent";

    private static final String HYDE_SYSTEM_INSTRUCTION = """
            Bạn là một chuyên gia truy xuất thông tin
            trong hệ thống RAG đa ngôn ngữ.

            Nhiệm vụ của bạn là tạo một đoạn tài liệu giả định
            có khả năng tương đồng với đoạn tài liệu nguồn
            chứa câu trả lời cho câu hỏi được cung cấp.

            Đoạn văn này được sử dụng để tạo embedding
            và tìm kiếm tài liệu, không phải để trả lời
            trực tiếp cho người dùng cuối.

            Yêu cầu:
            - Sử dụng cùng ngôn ngữ với câu hỏi.
            - Giữ nguyên tên riêng, thực thể, từ khóa
              và thuật ngữ quan trọng.
            - Tập trung vào đúng thông tin câu hỏi yêu cầu.
            - Viết ngắn gọn từ 2 đến 3 câu.
            - Sử dụng văn phong tài liệu khách quan,
              tự nhiên, giống một đoạn văn trong tài liệu nguồn.
            - Không mở rộng sang chủ đề không liên quan.
            - Không tự tạo tên riêng, số liệu hoặc mốc
              thời gian nếu không chắc chắn.
            - Không tự suy đoán thực thể chưa được xác định.
            - Không sử dụng lời chào, tiêu đề hoặc gạch đầu dòng.
            - Chỉ trả về đoạn văn giả định.
            """;

    private final HttpClient httpClient;
    private final String hydeApiKey;
    private final ObjectMapper objectMapper;

    @Autowired
    public HydeService(GeminiEnv geminiEnv) {

        this.hydeApiKey = geminiEnv.apiHydeKey;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        this.objectMapper = new ObjectMapper();
    }

    public String generateHypotheticalDocument(String question) {

        // Kiểm tra câu hỏi rỗng
        if (question == null || question.isBlank()) {
            return "";
        }

        String originalQuestion = question.trim();

        long start = System.currentTimeMillis();

        try {

            String requestBodyJson =
                    buildHydeRequestBody(originalQuestion);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_HYDE_URL))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", hydeApiKey)
                    .POST(HttpRequest.BodyPublishers
                            .ofString(requestBodyJson))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {

                log.warn(
                        "[HyDE] API error HTTP {}",
                        response.statusCode()
                );

                return originalQuestion;
            }

            JsonNode rootNode =
                    objectMapper.readTree(response.body());

            JsonNode candidate = rootNode
                    .path("candidates")
                    .path(0);

            JsonNode parts = candidate
                    .path("content")
                    .path("parts");

            StringBuilder result = new StringBuilder();

            if (parts.isArray()) {

                for (JsonNode part : parts) {

                    // Không lấy nội dung reasoning/thinking
                    if (part.path("thought").asBoolean(false)) {
                        continue;
                    }

                    String text = part.path("text").asText("");

                    if (!text.isBlank()) {
                        result.append(text).append(" ");
                    }
                }
            }

            String hypotheticalDoc =
                    result.toString().trim();

            // Fallback nếu Gemini không trả về văn bản
            if (hypotheticalDoc.isBlank()) {

                log.warn(
                        "[HyDE] Empty response. Finish reason: {}",
                        candidate.path("finishReason").asText()
                );

                return originalQuestion;
            }

            log.info(
                    "[HyDE] Generated successfully ({}ms)",
                    System.currentTimeMillis() - start
            );

            log.debug(
                    "[HyDE] Original query: {}",
                    originalQuestion
            );

            log.debug(
                    "[HyDE] Generated document: {}",
                    hypotheticalDoc
            );

            return hypotheticalDoc;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            log.warn("[HyDE] Request interrupted");

        } catch (Exception e) {

            log.warn(
                    "[HyDE] Generation failed. Fallback. Reason: {}",
                    e.getMessage()
            );
        }

        return originalQuestion;
    }

    private String buildHydeRequestBody(String question)
            throws Exception {

        ObjectNode root =
                objectMapper.createObjectNode();

        // System Instruction
        ObjectNode sysNode =
                root.putObject("system_instruction");

        ArrayNode sysParts =
                sysNode.putArray("parts");

        sysParts.addObject()
                .put("text", HYDE_SYSTEM_INSTRUCTION);

        // User Question
        ArrayNode contents =
                root.putArray("contents");

        ObjectNode contentObj =
                contents.addObject();

        contentObj.put("role", "user");

        ArrayNode contentParts =
                contentObj.putArray("parts");

        contentParts.addObject()
                .put("text", question);

        // Generation Configuration
        ObjectNode genConfig =
                root.putObject("generationConfig");

        genConfig.put("temperature", 0.1);

        genConfig.put("maxOutputTokens", 250);

        genConfig.put("candidateCount", 1);

        return objectMapper.writeValueAsString(root);
    }
}
