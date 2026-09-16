package com.mezon.classmanagement.backend.moderation.extractor;

import com.mezon.classmanagement.backend.domain_document.main.moderation.extractor.TxtTextExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TxtTextExtractorTest {
    private final TxtTextExtractor extractor = new TxtTextExtractor();

    @Test
    void shouldExtractInputTxtFile() throws Exception {

        // Đọc file input.txt trong src/main/resources
        ClassPathResource resource =
                new ClassPathResource("input.txt");

        assertTrue(resource.exists());

        // Chuyển file resource thành MultipartFile để mô phỏng file upload
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "input.txt",
                "text/plain",
                resource.getInputStream()
        );

        // Extract nội dung
        String result = extractor.extract(file);

        // Kiểm tra có lấy được nội dung
        assertNotNull(result);
        assertFalse(result.isBlank());

        System.out.println("===== NỘI DUNG EXTRACT =====");
        System.out.println(result);
    }
}
