package com.mezon.classmanagement.backend.rag.dataset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MultiHopExtractor {

    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Path datasetPath = Path.of("dataset/multihop");

        if (!Files.exists(datasetPath)) {
            datasetPath = Path.of("backend/dataset/multihop");
        }

        Path corpusPath = datasetPath.resolve("corpus.json");

        Path outputPath = datasetPath.resolve("documents");

        Files.createDirectories(outputPath);

        JsonNode corpus = mapper.readTree(
                corpusPath.toFile()
        );

        if (!corpus.isArray()) {
            throw new IllegalArgumentException(
                    "corpus.json phải là một JSON Array"
            );
        }

        ArrayNode mapping = mapper.createArrayNode();

        int count = 0;

        for (JsonNode document : corpus) {

            String title = document.path("title").asText();
            String body = document.path("body").asText();
            String url = document.path("url").asText();
            String source = document.path("source").asText();
            String category = document.path("category").asText();

            if (body.isBlank()) {
                continue;
            }

            count++;

            String datasetId = String.format(
                    "doc_%04d", count
            );

            String fileName = datasetId + ".txt";

            String content = title + "\n\n" + body;

            // Xuất tài liệu TXT
            Files.writeString(
                    outputPath.resolve(fileName),
                    content,
                    StandardCharsets.UTF_8
            );

            // Lưu thông tin ánh xạ
            ObjectNode item = mapper.createObjectNode();

            item.put("dataset_id", datasetId);
            item.put("file_name", fileName);
            item.put("title", title);
            item.put("url", url);
            item.put("source", source);
            item.put("category", category);
            item.putNull("database_id");

            mapping.add(item);

            System.out.println(
                    "Extracted: " + fileName
            );
        }

        // Xuất file ánh xạ tài liệu
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(
                        datasetPath.resolve(
                                "document_mapping.json"
                        ).toFile(),
                        mapping
                );

        System.out.println("======================");
        System.out.println("Total corpus: " + corpus.size());
        System.out.println("Extracted documents: " + count);
        System.out.println("Skipped: " + (corpus.size() - count));
        System.out.println("Extraction completed!");
    }
}