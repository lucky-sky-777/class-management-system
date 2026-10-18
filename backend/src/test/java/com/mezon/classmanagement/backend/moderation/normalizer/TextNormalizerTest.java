package com.mezon.classmanagement.backend.moderation.normalizer;

import com.mezon.classmanagement.backend.domain_document.main.moderation.normalizer.TextNormalizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextNormalizerTest {
    private final TextNormalizer normalizer =
            new TextNormalizer();

    @Test
    void shouldConvertToLowerCase() {

        String result =
                normalizer.normalize("GIẾT");

        assertEquals("giết", result);
    }

    @Test
    void shouldNormalizeSpaces() {

        String result =
                normalizer.normalize(
                        "  Đây     là    Java  "
                );

        assertEquals(
                "đây là java",
                result
        );
    }

    @Test
    void shouldKeepVietnameseCharacters() {

        String result =
                normalizer.normalize(
                        "TÀI LIỆU HỌC TẬP"
                );

        assertEquals(
                "tài liệu học tập",
                result
        );
    }

    @Test
    void nullShouldReturnEmpty() {

        String result =
                normalizer.normalize(null);

        assertEquals("", result);
    }

    @Test
    void blankShouldReturnEmpty() {

        String result =
                normalizer.normalize("   ");

        assertEquals("", result);
    }
}
