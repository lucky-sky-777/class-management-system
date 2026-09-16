package com.mezon.classmanagement.backend.moderation.keyword;

import com.mezon.classmanagement.backend.domain_document.main.moderation.keyword.KeywordModerationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeywordModerationServiceTest {
    private final KeywordModerationService moderationService =
            new KeywordModerationService();

    @Test
    void normalContent_shouldBeAllowed() {

        String content = "Tài liệu học Java rất hữu ích";

        assertTrue(moderationService.isAllowed(content));
    }

    @Test
    void bannedContent_shouldBeRejected() {

        String content = "Đây là cẹc";

        assertFalse(moderationService.isAllowed(content));
    }

    @Test
    void nullContent_shouldBeRejected() {

        assertFalse(moderationService.isAllowed(null));
    }

    @Test
    void blankContent_shouldBeRejected() {

        assertFalse(moderationService.isAllowed("   "));
    }
}
