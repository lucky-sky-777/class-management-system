package com.mezon.classmanagement.backend.moderation.extractor;

import com.mezon.classmanagement.backend.domain_document.main.moderation.extractor.DocxTextExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocxTextExtractorTest {
    private final DocxTextExtractor extractor = new DocxTextExtractor();

    @Test
    void shouldExtractHoChiMinhDocx() throws Exception {

        ClassPathResource resource =
                new ClassPathResource("Ho_Chi_Minh.docx");

        assertTrue(resource.exists());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Ho_Chi_Minh.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                resource.getInputStream()
        );

        String result = extractor.extract(file);

        assertNotNull(result);
        assertFalse(result.isBlank());

        System.out.println("===== NỘI DUNG DOCX =====");
        System.out.println(result);
    }
}
