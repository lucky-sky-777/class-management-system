package com.mezon.classmanagement.backend.rag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.repository.DocumentChunkRepository;
import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.embedding.util.EmbeddingUtils;
import com.mezon.classmanagement.backend.domain_document.main.rag.service.HydeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest
public class XquadRetrievalTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    @Autowired
    private HydeService hydeService; // Inject thêm HydeService

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final long VI_DOCUMENT_ID = 1040L;
    private static final long EN_DOCUMENT_ID = 1039L;

    // Giảm xuống 100 câu để tiết kiệm quota API
    private static final int QUESTION_LIMIT = 50;
    private static final int TOP_K = 10;

    // Nghỉ 4.5s khi bật HyDE để không vượt quá 15 RPM của Gemini Free Tier
    private static final long DELAY_BASELINE_MS = 1000;
    private static final long DELAY_HYDE_MS = 4500;

    // ============================
    // 1. TEST TIẾNG VIỆT (SO SÁNH)
    // ============================
    @Test
    public void testVietnameseComparison() throws Exception {
        runTest("vi", VI_DOCUMENT_ID, false); // Chạy Baseline trước
        runTest("vi", VI_DOCUMENT_ID, true);  // Chạy HyDE sau
    }

    // ============================
    // 2. TEST TIẾNG ANH (SO SÁNH)
    // ============================
    @Test
    public void testEnglishComparison() throws Exception {
        runTest("en", EN_DOCUMENT_ID, false);
        runTest("en", EN_DOCUMENT_ID, true);
    }

    // ============================
    // 3. CHẠY RIÊNG TỪNG CHẾ ĐỘ (NẾU MUỐN CHẠY LẺ)
    // ============================
    @Test
    public void testVietnameseHydeOnly() throws Exception {
        runTest("vi", VI_DOCUMENT_ID, true);
    }

    @Test
    public void testEnglishHydeOnly() throws Exception {
        runTest("en", EN_DOCUMENT_ID, true);
    }

    // --- Các hàm helper và cơ chế chấm điểm v2 ---
    private Path getDatasetPath() {
        Path path = Path.of("dataset");
        if (!Files.isDirectory(path)) {
            path = Path.of("backend", "dataset");
        }
        return path;
    }

    private Map<String, JsonNode> loadQuestions(String language) throws Exception {
        Path path = getDatasetPath().resolve("processed/questions_" + language + ".json");
        JsonNode root = MAPPER.readTree(path.toFile());
        Map<String, JsonNode> questions = new HashMap<>();
        for (JsonNode node : root) {
            questions.put(node.get("query_id").asText(), node);
        }
        return questions;
    }

    private List<JsonNode> selectQuestions(String language) throws Exception {
        Map<String, JsonNode> vi = loadQuestions("vi");
        Map<String, JsonNode> en = loadQuestions("en");

        List<String> commonIds = new ArrayList<>(vi.keySet());
        commonIds.retainAll(en.keySet());
        Collections.sort(commonIds);
        Collections.shuffle(commonIds, new Random(42)); // Giữ nguyên seed 42 để khớp 100 câu

        Map<String, JsonNode> target = language.equals("vi") ? vi : en;
        return commonIds.stream().limit(QUESTION_LIMIT).map(target::get).toList();
    }

    private String normalize(String text) {
        if (text == null || text.isBlank()) return "";
        return Normalizer.normalize(text, Normalizer.Form.NFC)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\s\\p{Z}]+", " ")
                .replaceAll("\\s*([\\p{P}])\\s*", "$1")
                .trim();
    }

    private boolean isRelevant(String chunkContent, JsonNode question) {
        String content = normalize(chunkContent);
        String context = normalize(question.path("context").asText());
        if (content.isBlank() || context.isBlank()) return false;

        for (JsonNode answer : question.path("answers")) {
            String answerText = normalize(answer.path("text").asText());
            if (answerText.isBlank() || !content.contains(answerText)) continue;
            if (content.contains(context) || context.contains(content)) return true;

            int index = context.indexOf(answerText);
            while (index >= 0) {
                int answerEnd = index + answerText.length();
                int start = Math.max(0, index - 50);
                int end = Math.min(context.length(), answerEnd + 50);
                if (content.contains(context.substring(start, end))) return true;
                if (index - start >= 30 &&
                        content.contains(context.substring(start, answerEnd))) return true;
                if (end - answerEnd >= 30 &&
                        content.contains(context.substring(index, end))) return true;
                index = context.indexOf(answerText, index + 1);
            }
        }
        return false;
    }

    private void writeLog(Path logFile, String message) throws Exception {
        Files.writeString(
                logFile,
                message + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    // ============================
    // CHẠY KIỂM THỬ CHÍNH
    // ============================
    private void runTest(String language, long documentId, boolean useHyde) throws Exception {
        List<JsonNode> questions = selectQuestions(language);
        String mode = useHyde ? "hyde" : "baseline";

        Path resultDir = getDatasetPath().resolve("results_v2");
        Files.createDirectories(resultDir);

        Path logFile = resultDir.resolve("retrieval_" + language + "_" + mode + ".log");
        Path csvFile = resultDir.resolve("retrieval_" + language + "_" + mode + ".csv");

        Files.writeString(logFile, "", StandardCharsets.UTF_8);
        Files.writeString(
                csvFile,
                "query_id,rank,hit5,hit10,mrr,hyde_ms,embedding_ms,retrieval_ms,status\n",
                StandardCharsets.UTF_8
        );

        writeLog(logFile, "START: " + LocalDateTime.now());
        writeLog(logFile, "Mode: " + mode.toUpperCase());
        writeLog(logFile, "Language: " + language);
        writeLog(logFile, "Document ID: " + documentId);
        writeLog(logFile, "Total questions: " + questions.size());

        int successCount = 0;
        int errorCount = 0;
        int totalHit1 = 0;
        int totalHit5 = 0;
        int totalHit10 = 0;
        double totalMRR = 0;

        List<Double> retrievalLatencies = new ArrayList<>();
        List<Double> hydeLatencies = new ArrayList<>();
        List<Double> totalLatencies = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            JsonNode question = questions.get(i);
            String queryId = question.get("query_id").asText();
            String originalQuery = question.get("question").asText();

            StringBuilder log = new StringBuilder();
            log.append("\n================================\n");
            log.append("Question: ").append(i + 1).append(" [").append(mode.toUpperCase()).append("]\n");
            log.append("Query ID: ").append(queryId).append("\n");
            log.append("Original Query: ").append(originalQuery).append("\n");
            log.append("Ground Truth Answers: ").append(question.path("answers")).append("\n");
            log.append("Ground Truth Context: ").append(question.path("context").asText()).append("\n");

            try {
                // 0. Bước HyDE (Nếu bật)
                double hydeMs = 0.0;
                String textToEmbed = originalQuery;

                if (useHyde) {
                    long hydeStart = System.nanoTime();
                    textToEmbed = hydeService.generateHypotheticalDocument(originalQuery);
                    hydeMs = (System.nanoTime() - hydeStart) / 1_000_000.0;
                    log.append("HyDE Generated Doc: ").append(textToEmbed).append("\n");
                }

                // 1. Tạo embedding
                long embeddingStart = System.nanoTime();
                float[] queryEmbedding = embeddingService
                        .embedSingleQueryText(textToEmbed)
                        .getValues();
                double embeddingMs = (System.nanoTime() - embeddingStart) / 1_000_000.0;

                // 2. Truy xuất Top K chunk
                long retrievalStart = System.nanoTime();
                List<DocumentChunk> chunks = documentChunkRepository.searchInOneDocument(
                        documentId,
                        queryEmbedding,
                        TOP_K
                );
                double retrievalMs = (System.nanoTime() - retrievalStart) / 1_000_000.0;

                // 3. Kiểm tra thứ hạng
                int firstRelevantRank = 0;
                for (int j = 0; j < chunks.size(); j++) {
                    DocumentChunk chunk = chunks.get(j);
                    boolean relevant = isRelevant(chunk.getContent(), question);
                    if (relevant && firstRelevantRank == 0) {
                        firstRelevantRank = j + 1;
                    }

                    double similarity = EmbeddingUtils.cosine(
                            queryEmbedding,
                            chunk.getEmbedding().getValues()
                    );

                    log.append("\n--- TOP ").append(j + 1).append(" ---\n");
                    log.append("Similarity: ").append(similarity).append("\n");
                    log.append("Relevant: ").append(relevant).append("\n");
                    log.append("Content:\n").append(chunk.getContent()).append("\n");
                }

                // 4. Tính chỉ số
                int hit5 = (firstRelevantRank > 0 && firstRelevantRank <= 5) ? 1 : 0;
                int hit10 = (firstRelevantRank > 0 && firstRelevantRank <= 10) ? 1 : 0;
                double mrr = (firstRelevantRank == 0) ? 0.0 : 1.0 / firstRelevantRank;

                log.append("\nRESULT\n");
                log.append("First relevant rank: ").append(firstRelevantRank).append("\n");
                log.append("Hit@5: ").append(hit5).append(" | Hit@10: ").append(hit10).append(" | MRR: ").append(mrr).append("\n");
                log.append(String.format(Locale.US, "Time -> HyDE: %.2f ms | Embed: %.2f ms | DB: %.2f ms%n", hydeMs, embeddingMs, retrievalMs));

                String csvRow = String.format(
                        Locale.US,
                        "%s,%d,%d,%d,%.6f,%.3f,%.3f,%.3f,SUCCESS%n",
                        queryId, firstRelevantRank, hit5, hit10, mrr, hydeMs, embeddingMs, retrievalMs
                );
                Files.writeString(csvFile, csvRow, StandardCharsets.UTF_8, StandardOpenOption.APPEND);

                successCount++;
                totalHit1 += firstRelevantRank == 1 ? 1 : 0;
                totalLatencies.add(hydeMs + embeddingMs + retrievalMs);
                totalHit5 += hit5;
                totalHit10 += hit10;
                totalMRR += mrr;
                retrievalLatencies.add(retrievalMs);
                if (useHyde) hydeLatencies.add(hydeMs);

                System.out.printf(
                        "[%s - %s] %d/%d | Rank: %d | HyDE: %.0fms | DB: %.1fms%n",
                        language.toUpperCase(), mode.toUpperCase(), i + 1, questions.size(),
                        firstRelevantRank, hydeMs, retrievalMs
                );

            } catch (Exception e) {
                errorCount++;
                log.append("ERROR: ").append(e).append("\n");
                Files.writeString(csvFile, queryId + ",,,,,,,,ERROR\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
                System.err.printf("[%s - %s] %d/%d | ERROR: %s%n", language.toUpperCase(), mode.toUpperCase(), i + 1, questions.size(), e.getMessage());
            }

            writeLog(logFile, log.toString());

            long sleepTime = useHyde ? DELAY_HYDE_MS : DELAY_BASELINE_MS;
            if (i + 1 < questions.size()) {
                Thread.sleep(sleepTime);
            }
        }

        // 6. Tổng hợp kết quả
        Collections.sort(retrievalLatencies);
        Collections.sort(hydeLatencies);
        Collections.sort(totalLatencies);
        double avgTotal = totalLatencies.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double p95Total = totalLatencies.isEmpty() ? 0 : totalLatencies.get((int) Math.ceil(totalLatencies.size() * 0.95) - 1);

        double p95Retrieval = retrievalLatencies.isEmpty() ? 0 : retrievalLatencies.get((int) Math.ceil(retrievalLatencies.size() * 0.95) - 1);
        double avgHydeMs = hydeLatencies.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        String summary = String.format(
                Locale.US,
                """
                
                ================================
                SUMMARY [%s - %s]
                ================================
                Document ID: %d
                Total questions: %d | Success: %d | Errors: %d
                Hit@1:  %.4f
                Hit@5:  %.4f
                Hit@10: %.4f
                MRR@10: %.4f
                Avg HyDE Latency: %.2f ms
                P95 DB Retrieval: %.2f ms
                Avg Total Latency: %.2f ms
                P95 Total Latency: %.2f ms
                Finished: %s
                """,
                language.toUpperCase(), mode.toUpperCase(),
                documentId, questions.size(), successCount, errorCount,
                successCount == 0 ? 0 : (double) totalHit1 / successCount,
                successCount == 0 ? 0 : (double) totalHit5 / successCount,
                successCount == 0 ? 0 : (double) totalHit10 / successCount,
                successCount == 0 ? 0 : totalMRR / successCount,
                avgHydeMs, p95Retrieval, avgTotal, p95Total, LocalDateTime.now()
        );

        writeLog(logFile, summary);
        System.out.println(summary);
    }
}
