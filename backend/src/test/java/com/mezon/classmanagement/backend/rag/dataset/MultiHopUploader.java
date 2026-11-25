package com.mezon.classmanagement.backend.rag.dataset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.nio.file.StandardOpenOption;

public class MultiHopUploader {

    // Cần sửa theo API thực tế của bạn
    private static final String UPLOAD_URL =
            "http://localhost:8080/api/documents/upload";

    private static final String TOKEN =
            "YOUR_ACCESS_TOKEN";

    // Số file tối đa upload trong một lần chạy
    private static final int LIMIT = 5;

    // Thời gian nghỉ giữa hai lần upload
    private static final long DELAY_MS = 3000;

    private static final ObjectMapper mapper =
            new ObjectMapper();

    private static final RestTemplate restTemplate =
            new RestTemplate();

    public static void main(String[] args) throws Exception {

        Path datasetPath = Path.of("dataset/multihop");

        if (!Files.exists(datasetPath)) {
            datasetPath = Path.of("backend/dataset/multihop");
        }

        Path documentsPath = datasetPath.resolve("documents");

        Path mappingPath = datasetPath.resolve(
                "document_mapping.json"
        );

        Path logPath = datasetPath.resolve("upload.log");

        JsonNode mapping = mapper.readTree(
                mappingPath.toFile()
        );

        int success = 0;
        int failed = 0;
        int skipped = 0;
        int attempted = 0;

        for (JsonNode node : mapping) {

            ObjectNode document = (ObjectNode) node;

            String fileName = document.path("file_name").asText();

            // Bỏ qua tài liệu đã có ID
            if (document.hasNonNull("database_id")) {
                skipped++;
                continue;
            }

            if (attempted >= LIMIT) {
                break;
            }

            Path filePath = documentsPath.resolve(fileName);

            attempted++;

            try {

                writeLog(logPath, "Uploading: " + fileName);

                long documentId = uploadFile(filePath);

                // Cập nhật ID tài liệu
                document.put("database_id", documentId);

                // Lưu tiến độ sau mỗi lần thành công
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValue(mappingPath.toFile(), mapping);

                success++;

                writeLog(
                        logPath,
                        "SUCCESS: " + fileName
                                + " | ID: " + documentId
                );

            } catch (Exception e) {

                failed++;

                writeLog(
                        logPath,
                        "FAILED: " + fileName
                                + " | " + e.getMessage()
                );

                // Dừng để kiểm tra trước khi tiếp tục
                break;
            }

            Thread.sleep(DELAY_MS);
        }

        System.out.println("==========================");
        System.out.println("UPLOAD SUMMARY");
        System.out.println("Success: " + success);
        System.out.println("Failed: " + failed);
        System.out.println("Skipped: " + skipped);
        System.out.println("==========================");
    }

    private static long uploadFile(Path filePath)
            throws Exception {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.MULTIPART_FORM_DATA
        );

        headers.setBearerAuth(TOKEN);

        MultiValueMap<String, Object> body =
                new LinkedMultiValueMap<>();

        body.add(
                "file",
                new FileSystemResource(filePath.toFile())
        );

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        UPLOAD_URL,
                        HttpMethod.POST,
                        request,
                        String.class
                );

        JsonNode result = mapper.readTree(
                response.getBody()
        );

        // Cần sửa theo cấu trúc response thực tế
        JsonNode idNode = result.path("data").path("id");

        if (!idNode.isIntegralNumber() || idNode.asLong() <= 0) {
            throw new IllegalStateException(
                    "Không tìm thấy Document ID: "
                            + response.getBody()
            );
        }

        return idNode.asLong();
    }

    private static void writeLog(Path logPath, String message)
            throws Exception {

        String log = LocalDateTime.now()
                + " | " + message
                + System.lineSeparator();

        System.out.print(log);

        Files.writeString(
                logPath,
                log,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }
}