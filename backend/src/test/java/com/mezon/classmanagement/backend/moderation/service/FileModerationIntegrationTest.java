package com.mezon.classmanagement.backend.moderation.service;

import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.FileDocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphWithoutOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.main.moderation.keyword.KeywordModerationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


// Integration Test kiểm tra quá trình đọc file DOCX,
// chia nội dung thành các chunk và kiểm duyệt keyword bị cấm.

@SpringJUnitConfig(ModerationTestConfig.class)
public class FileModerationIntegrationTest {

    @Autowired
    private SplitByParagraphWithoutOverlapStrategy splitStrategy;

    @Autowired
    private KeywordModerationService keywordModerationService;

    @Test
    void readFile_shouldBeProcessed() throws Exception {

        ClassPathResource resource =
                new ClassPathResource("Ho_Chi_Minh.docx");

        MultipartFile file = new MockMultipartFile(
                "file",
                "Ho_Chi_Minh.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                resource.getInputStream()
        );

        Path tempPath = Files.createTempFile("moderation-", ".docx");
        file.transferTo(tempPath);

       // try {
            File tempFile = tempPath.toFile();

            DocumentSource documentSource =
                    new FileDocumentSource(tempFile);

            List<String> chunks =
                    splitStrategy.getTextList(documentSource);

            assertNotNull(chunks);
            assertFalse(chunks.isEmpty());

            boolean allowed =
                    keywordModerationService.isAllowed(chunks);

            System.out.println("Số lượng chunks: " + chunks.size());
            System.out.println("Moderation result: " + allowed);

            chunks.forEach(System.out::println);

            assertFalse(allowed);

//        }
//        finally {
//            Files.deleteIfExists(tempPath);
//        }
    }
}
