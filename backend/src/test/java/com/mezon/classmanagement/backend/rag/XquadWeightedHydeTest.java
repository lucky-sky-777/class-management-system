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
public class XquadWeightedHydeTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    @Autowired
    private HydeService hydeService; // Inject thêm HydeService

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final long VI_DOCUMENT_ID = 1040L;
    private static final long EN_DOCUMENT_ID = 1039L;

    private static final int QUESTION_LIMIT = 50;
    private static final int TOP_K = 10;
    private static final long DELAY_MS = 4500;
    private static final double[] ALPHAS = {1.0, 0.9, 0.8, 0.7, 0.6, 0.5, 0.3, 0.0};

    @Test
    public void testVietnameseWeightedHyde() throws Exception {
        runTest("vi", VI_DOCUMENT_ID);
    }

    @Test
    public void testEnglishWeightedHyde() throws Exception {
        runTest("en", EN_DOCUMENT_ID);
    }

    private float[] normalizeVector(float[] vector) {
        double norm = 0;
        for (float v : vector) norm += (double) v * v;
        norm = Math.sqrt(norm);
        if (!Double.isFinite(norm) || norm == 0) {
            throw new IllegalArgumentException("Invalid vector norm");
        }
        float[] result = vector.clone();
        for (int i = 0; i < result.length; i++) result[i] /= norm;
        return result;
    }

    private float[] combineVectors(float[] q, float[] h, double alpha) {
        if (q.length != h.length) throw new IllegalArgumentException("Dimension mismatch");
        // Keep endpoint baselines identical to the original retrieval test.
        if (alpha == 1.0) return q;
        if (alpha == 0.0) return h;
        float[] nq = normalizeVector(q);
        float[] nh = normalizeVector(h);
        float[] result = new float[q.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (float) (alpha * nq[i] + (1 - alpha) * nh[i]);
        }
        return normalizeVector(result);
    }

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

    private void runTest(String language, long documentId) throws Exception {
        List<JsonNode> questions = selectQuestions(language);
        Path dir = getDatasetPath().resolve("results_weighted_hyde")
                .resolve(language + "_" + UUID.randomUUID());
        Files.createDirectories(dir);
        Path csv = dir.resolve("comparison.csv");
        Path log = dir.resolve("retrieval.log");
        Files.writeString(csv, "query_id,alpha,rank,hit1,hit5,hit10,mrr,hyde_ms,embedding_ms,retrieval_ms,fusion_ms,total_ms,fallback,status\n");
        double[][] sums = new double[ALPHAS.length][5];
        int[] successes = new int[ALPHAS.length];
        int fallbackCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            JsonNode question = questions.get(i);
            String id = question.path("query_id").asText();
            String query = question.path("question").asText();
            writeLog(log, "\nQuery ID: " + id + "\nOriginal Query: " + query
                    + "\nGround Truth Answers: " + question.path("answers")
                    + "\nGround Truth Context: " + question.path("context").asText());
            float[] q, h;
            double qMs, hMs, hydeMs;
            boolean fallback;
            try {
                long start = System.nanoTime();
                q = embeddingService.embedSingleQueryText(query).getValues();
                qMs = elapsed(start);
                start = System.nanoTime();
                String doc = hydeService.generateHypotheticalDocument(query);
                hydeMs = elapsed(start);
                fallback = doc == null || doc.isBlank() || doc.trim().equals(query.trim());
                if (fallback) fallbackCount++;
                writeLog(log, "HyDE Generated Doc: " + doc + "\nFallback: " + fallback);
                start = System.nanoTime();
                h = fallback ? q : embeddingService.embedSingleQueryText(doc).getValues();
                hMs = fallback ? 0 : elapsed(start);
            } catch (Exception e) {
                writeLog(log, "Preparation ERROR: " + e);
                for (double alpha : ALPHAS) writeError(csv, id, alpha);
                if (i + 1 < questions.size()) Thread.sleep(DELAY_MS);
                continue;
            }
            // Rotate search order to reduce systematic warm-up bias.
            for (int offset = 0; offset < ALPHAS.length; offset++) {
                int a = (i + offset) % ALPHAS.length;
                double alpha = ALPHAS[a];
                try {
                    long start = System.nanoTime();
                    float[] vector = combineVectors(q, h, alpha);
                    double fusionMs = elapsed(start);
                    start = System.nanoTime();
                    List<DocumentChunk> chunks = documentChunkRepository.searchInOneDocument(documentId, vector, TOP_K);
                    double dbMs = elapsed(start);
                    int rank = 0;
                    StringBuilder details = new StringBuilder("\nAlpha: " + alpha + "\n");
                    for (int j = 0; j < chunks.size(); j++) {
                        DocumentChunk chunk = chunks.get(j);
                        boolean relevant = isRelevant(chunk.getContent(), question);
                        if (relevant && rank == 0) rank = j + 1;
                        details.append("TOP ").append(j + 1).append(" | Similarity: ")
                                .append(EmbeddingUtils.cosine(vector, chunk.getEmbedding().getValues()))
                                .append(" | Relevant: ").append(relevant).append('\n')
                                .append(chunk.getContent()).append('\n');
                    }
                    int hit1 = rank == 1 ? 1 : 0;
                    int hit5 = rank > 0 && rank <= 5 ? 1 : 0;
                    int hit10 = rank > 0 && rank <= 10 ? 1 : 0;
                    double mrr = hit10 == 1 ? 1.0 / rank : 0;
                    double gen = alpha == 1 ? 0 : hydeMs;
                    double embed = alpha == 1 ? qMs : alpha == 0 && !fallback ? hMs : qMs + hMs;
                    double total = gen + embed + dbMs + fusionMs;
                    Files.writeString(csv, String.format(Locale.US,
                            "%s,%.1f,%d,%d,%d,%d,%.6f,%.3f,%.3f,%.3f,%.3f,%.3f,%s,SUCCESS%n",
                            id, alpha, rank, hit1, hit5, hit10, mrr, gen, embed, dbMs, fusionMs, total, fallback && alpha != 1), StandardOpenOption.APPEND);
                    writeLog(log, details + "First relevant rank: " + rank);
                    successes[a]++;
                    sums[a][0] += hit1; sums[a][1] += hit5; sums[a][2] += hit10;
                    sums[a][3] += mrr; sums[a][4] += total;
                    System.out.printf(Locale.US, "%d/%d | alpha=%.1f | rank=%d%n", i+1, questions.size(), alpha, rank);
                } catch (Exception e) {
                    writeLog(log, "Alpha " + alpha + " ERROR: " + e);
                    writeError(csv, id, alpha);
                }
            }
            if (i + 1 < questions.size()) Thread.sleep(DELAY_MS);
        }
        StringBuilder summary = new StringBuilder("alpha,success,errors,hit1,hit5,hit10,mrr,avg_total_ms\n");
        for (int a = 0; a < ALPHAS.length; a++) {
            int n = successes[a];
            summary.append(String.format(Locale.US, "%.1f,%d,%d,%.6f,%.6f,%.6f,%.6f,%.3f%n",
                    ALPHAS[a], n, questions.size()-n, avg(sums[a][0],n), avg(sums[a][1],n),
                    avg(sums[a][2],n), avg(sums[a][3],n), avg(sums[a][4],n)));
        }
        Files.writeString(dir.resolve("summary.csv"), summary.toString());
        writeLog(log, "Fallback count: " + fallbackCount + "\n" + summary);
        System.out.println(summary);
        System.out.println("Results: " + dir.toAbsolutePath());
    }

    private double elapsed(long start) { return (System.nanoTime() - start) / 1_000_000.0; }
    private double avg(double sum, int n) { return n == 0 ? Double.NaN : sum / n; }
    private void writeError(Path csv, String id, double alpha) throws Exception {
        Files.writeString(csv, String.format(Locale.US, "%s,%.1f,,,,,,,,,,,,ERROR%n", id, alpha), StandardOpenOption.APPEND);
    }
}
