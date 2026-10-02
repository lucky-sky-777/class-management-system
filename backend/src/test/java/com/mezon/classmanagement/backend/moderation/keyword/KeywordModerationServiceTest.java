package com.mezon.classmanagement.backend.moderation.keyword;

import com.mezon.classmanagement.backend.domain_document.main.moderation.keyword.KeywordModerationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


// Unit Test kiểm tra chức năng kiểm duyệt keyword trong nội dung các chunk.

public class KeywordModerationServiceTest {

    private final KeywordModerationService moderationService =
            new KeywordModerationService();

    @Test
    void normalContent_shouldBeAllowed() {

        List<String> chunks = List.of(
                "Tài liệu học Java rất hữu ích",
                "Spring Boot là một framework phổ biến",
                "Sinh viên có thể sử dụng Java để phát triển backend"
        );

        assertTrue(moderationService.isAllowed(chunks));
    }

    @Test
    void bannedContent_shouldBeRejected() {

        List<String> chunks = List.of(
                "Tài liệu học Java rất hữu ích",
                "Đây là á đù",
                "Spring Boot là framework"
        );

        assertFalse(moderationService.isAllowed(chunks));
    }

    @Test
    void bannedContentInLaterChunk_shouldBeRejected() {

        List<String> chunks = List.of(
                "Tài liệu học Java rất hữu ích",
                "Spring Boot là framework",
                "Đây là á đù"
        );

        assertFalse(moderationService.isAllowed(chunks));
    }

    @Test
    void nullChunks_shouldBeRejected() {

        assertFalse(moderationService.isAllowed(null));
    }

    @Test
    void emptyChunks_shouldBeRejected() {

        assertFalse(moderationService.isAllowed(List.of()));
    }

    @Test
    void blankChunk_shouldBeIgnored() {

        List<String> chunks = List.of(
                "   ",
                "Tài liệu học Java rất hữu ích",
                "\n\t"
        );

        assertTrue(moderationService.isAllowed(chunks));
    }
}
