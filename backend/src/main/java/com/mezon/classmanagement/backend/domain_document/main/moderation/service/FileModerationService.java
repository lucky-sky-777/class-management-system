package com.mezon.classmanagement.backend.domain_document.main.moderation.service;

import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphWithoutOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.main.moderation.keyword.KeywordModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileModerationService {

    private final ChunkService chunkService;
    private final KeywordModerationService keywordModerationService;
    private final SplitByParagraphWithoutOverlapStrategy splitStrategy;

    public boolean isAllowed(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return false;
        }

        List<String> chunks =
                chunkService.getChunkListFromMultipartFile(
                        file,
                        splitStrategy
                );

        return keywordModerationService.isAllowed(chunks);
    }
}
