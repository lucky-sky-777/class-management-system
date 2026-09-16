package com.mezon.classmanagement.backend.domain_document.main.moderation.keyword;

import com.mezon.classmanagement.backend.domain_document.main.moderation.normalizer.TextNormalizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class KeywordModerationService {
    //ds từ bị cấm
    private final List<String> bannedWords;

    private final TextNormalizer textNormalizer;

    public KeywordModerationService() {

        this.textNormalizer = new TextNormalizer();

        try {

            ClassPathResource resource =
                    new ClassPathResource("banned_words.txt");

            bannedWords = resource
                    .getContentAsString(StandardCharsets.UTF_8)
                    .lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .map(textNormalizer::normalize)
                    .toList();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Không thể đọc file banned_words.txt",
                    e
            );
        }
    }

    public boolean isAllowed(String content) {

        String text =
                textNormalizer.normalize(content);

        if (text.isBlank()) {
            return false;
        }

        for (String word : bannedWords) {

            if (text.contains(word)) {
                return false;
            }
        }

        return true;
    }
}
