package com.mezon.classmanagement.backend.domain_document.main.moderation.extractor;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileTextExtractorFactory {
    private final TxtTextExtractor txtTextExtractor;
    private final DocxTextExtractor docxTextExtractor;
    private final PdfTextExtractor pdfTextExtractor;

    public FileTextExtractorFactory(TxtTextExtractor txtTextExtractor,  DocxTextExtractor docxTextExtractor, PdfTextExtractor pdfTextExtractor) {
        this.txtTextExtractor = txtTextExtractor;
        this.docxTextExtractor = docxTextExtractor;
        this.pdfTextExtractor = pdfTextExtractor;
    }

    public FileTextExtractor getExtractor(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if(filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("File không có phần mở rộng ");
        }

        String extension = filename
                .substring(filename.lastIndexOf(".") +1)
                .toLowerCase();

        return switch (extension) {
            case "txt" -> txtTextExtractor;
            case "pdf" -> pdfTextExtractor;
            case "docx" -> docxTextExtractor;
            default -> throw new IllegalArgumentException(
                    "Định dạng file không được hỗ trợ: " + extension
            );
        };
    }
}
