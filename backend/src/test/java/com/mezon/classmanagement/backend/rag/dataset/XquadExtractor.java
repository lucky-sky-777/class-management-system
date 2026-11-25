package com.mezon.classmanagement.backend.rag.dataset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class XquadExtractor {

    public static void main(String[] args) throws Exception {
        extract("vi");
        extract("en");
    }

    public static void extract(String lang) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Path datasetDir = Path.of("dataset");

        // Kiểm tra trường hợp chạy từ thư mục gốc project
        if (!Files.isDirectory(datasetDir)) {
            datasetDir = Path.of("backend/dataset");
        }

        Path inputFile = datasetDir.resolve(
                "xquad." + lang + ".json"
        );

        System.out.println("Working directory: "
                + System.getProperty("user.dir"));

        System.out.println("Input file: "
                + inputFile.toAbsolutePath());

        System.out.println("File exists: "
                + Files.exists(inputFile));

        if (!Files.exists(inputFile)) {
            throw new IllegalStateException(
                    "Không tìm thấy file: " + inputFile.toAbsolutePath()
            );
        }

        JsonNode root = mapper.readTree(inputFile.toFile());

        Path outputDir = datasetDir.resolve("processed");

        Files.createDirectories(outputDir);

        StringBuilder content = new StringBuilder();

        ArrayNode questions = mapper.createArrayNode();

        int paragraphIndex = 0;

        for (JsonNode article : root.get("data")) {

            for (JsonNode paragraph : article.get("paragraphs")) {

                String context = paragraph.get("context").asText();

                content.append(context).append("\n\n");

                for (JsonNode qa : paragraph.get("qas")) {

                    ObjectNode question = mapper.createObjectNode();

                    question.put("query_id", qa.get("id").asText());

                    question.put("paragraph_id", paragraphIndex);

                    question.put("question",
                            qa.get("question").asText());

                    question.set("answers", qa.get("answers"));

                    question.put("context", context);

                    questions.add(question);
                }

                paragraphIndex++;
            }
        }

        // Xuất toàn bộ context thành một file TXT
        Files.writeString(
                outputDir.resolve("documents_" + lang + ".txt"),
                content.toString(),
                StandardCharsets.UTF_8
        );

        // Xuất bộ câu hỏi và đáp án thành JSON
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(
                        outputDir.resolve(
                                "questions_" + lang + ".json"
                        ).toFile(),
                        questions
                );

        System.out.println("-------------------------");
        System.out.println("Language: " + lang);
        System.out.println("Paragraphs: " + paragraphIndex);
        System.out.println("Questions: " + questions.size());
        System.out.println("Extract successfully!");
    }
}