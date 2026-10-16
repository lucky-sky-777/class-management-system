package com.mezon.classmanagement.backend.moderation.extractor;

import com.mezon.classmanagement.backend.domain_document.main.moderation.extractor.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileTextExtractorFactoryTest {
    private final TxtTextExtractor txtTextExtractor = new TxtTextExtractor();

    private final PdfTextExtractor pdfTextExtractor = new PdfTextExtractor();

    private final DocxTextExtractor docxTextExtractor = new DocxTextExtractor();

    private final FileTextExtractorFactory factory =
            new FileTextExtractorFactory(
                    txtTextExtractor,
                    docxTextExtractor,
                    pdfTextExtractor
            );

    @Test
    void shouldReturnTxtExtractor() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "input.txt",
                "text/plain",
                "Hello Java".getBytes()
        );

        FileTextExtractor extractor =
                factory.getExtractor(file);

        assertInstanceOf(
                TxtTextExtractor.class,
                extractor
        );
    }

    @Test
    void shouldReturnPdfExtractor() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                new byte[0]
        );

        FileTextExtractor extractor =
                factory.getExtractor(file);

        assertInstanceOf(
                PdfTextExtractor.class,
                extractor
        );
    }

    @Test
    void shouldReturnDocxExtractor() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                new byte[0]
        );

        FileTextExtractor extractor =
                factory.getExtractor(file);

        assertInstanceOf(
                DocxTextExtractor.class,
                extractor
        );
    }

    @Test
    void unsupportedExtension_shouldThrowException() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                new byte[0]
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.getExtractor(file)
        );
    }

    @Test
    void fileWithoutExtension_shouldThrowException() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document",
                "text/plain",
                new byte[0]
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.getExtractor(file)
        );
    }
}
