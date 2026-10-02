package com.mezon.classmanagement.backend.moderation.ai;

import com.mezon.classmanagement.backend.domain_document.main.moderation.ai.OpenAiModerationService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


// Test OpenAiModerationService bằng OpenAI Moderation API với file DOCX.

class OpenAiModerationFileTest {

    private final OpenAiModerationService service =
            new OpenAiModerationService();

    @Test
    void shouldAllowNormalDocxFile() throws Exception {

        ClassPathResource resource =
                new ClassPathResource("Ho_Chi_Minh.docx");

        StringBuilder text = new StringBuilder();

        try (InputStream inputStream = resource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {

            document.getParagraphs()
                    .forEach(paragraph ->
                            text.append(paragraph.getText())
                                    .append("\n")
                    );
        }

        boolean result = service.isAllowed(text.toString());

        System.out.println("Moderation result: " + result);

        assertTrue(result);
    }

    @Test
    void shouldRejectSelfHarmVietnameseDocxFile() throws Exception {

        ClassPathResource resource =
                new ClassPathResource("OpenAI_Moderation_Flag_Test.docx");

        StringBuilder text = new StringBuilder();

        try (InputStream inputStream = resource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {

            document.getParagraphs()
                    .forEach(paragraph ->
                            text.append(paragraph.getText())
                                    .append("\n")
                    );
        }

        boolean result = service.isAllowed(text.toString());

        System.out.println("Moderation result: " + result);

        assertFalse(result);
    }
}
