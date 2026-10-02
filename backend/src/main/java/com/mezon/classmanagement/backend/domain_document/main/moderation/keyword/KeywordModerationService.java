package com.mezon.classmanagement.backend.domain_document.main.moderation.keyword;

import com.mezon.classmanagement.backend.domain_document.main.moderation.normalizer.TextNormalizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class KeywordModerationService {

    private final TextNormalizer textNormalizer;
    private final AhoCorasickMatcher matcher;

    public KeywordModerationService() {

        this.textNormalizer = new TextNormalizer();

        try {
            ClassPathResource resource =
                    new ClassPathResource("banned_words.txt");

            List<String> bannedWords = resource
                    .getContentAsString(StandardCharsets.UTF_8)
                    .lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .map(textNormalizer::normalize)
                    .toList();

            this.matcher = new AhoCorasickMatcher(bannedWords);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Không thể đọc file banned_words.txt",
                    e
            );
        }
    }

    public boolean isAllowed(List<String> chunks) {
        boolean allowed = true;
        if (chunks == null || chunks.isEmpty()) {
            return false;
        }

        for (String chunk : chunks) {

            String text = textNormalizer.normalize(chunk);

            // Chunk rỗng thì bỏ qua
            if (text.isBlank()) {
                continue;
            }

            List<String> matchedWords = matcher.findAll(text);

            if (!matchedWords.isEmpty()) {
                return false;
            }
        }

        return allowed;
    }
}
