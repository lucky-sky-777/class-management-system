package com.mezon.classmanagement.backend.moderation.service;


import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphWithoutOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.main.moderation.keyword.KeywordModerationService;
import com.mezon.classmanagement.backend.domain_document.main.moderation.service.FileModerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileModerationServiceTest {

    @Mock
    private ChunkService chunkService;

    @Mock
    private SplitByParagraphWithoutOverlapStrategy splitStrategy;

    private final KeywordModerationService keywordModerationService =
            new KeywordModerationService();

    private FileModerationService fileModerationService;

    @BeforeEach
    void setUp() {
        fileModerationService = new FileModerationService(
                chunkService,
                keywordModerationService,
                splitStrategy
        );
    }

    @Test
    void allowedFile_shouldReturnTrue() throws IOException {

        ClassPathResource resource =
                new ClassPathResource("triethocmaclenin.docx");

        MultipartFile file = new MockMultipartFile(
                "file",
                "triethocmaclenin.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                resource.getInputStream()
        );

        List<String> chunks = List.of(
                "Tài liệu học triết học",
                "Nội dung về chủ nghĩa duy vật"
        );

        when(chunkService.getChunkListFromMultipartFile(file, splitStrategy))
                .thenReturn(chunks);

        assertTrue(fileModerationService.isAllowed(file));
    }

    @Test
    void bannedFile_shouldReturnFalse() throws IOException {

        ClassPathResource resource =
                new ClassPathResource("triethocmaclenin.docx");

        MultipartFile file = new MockMultipartFile(
                "file",
                "triethocmaclenin.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                resource.getInputStream()
        );

        List<String> chunks = List.of(
                "Tài liệu học triết học",
                "Đây là cẹc"
        );

        when(chunkService.getChunkListFromMultipartFile(file, splitStrategy))
                .thenReturn(chunks);

        assertFalse(fileModerationService.isAllowed(file));
    }

    @Test
    void nullFile_shouldReturnFalse() {

        assertFalse(
                fileModerationService.isAllowed(null)
        );
    }

    @Test
    void emptyFile_shouldReturnFalse() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        assertFalse(
                fileModerationService.isAllowed(file)
        );
    }
}
