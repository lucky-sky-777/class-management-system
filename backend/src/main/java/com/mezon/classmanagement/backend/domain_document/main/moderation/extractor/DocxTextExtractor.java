package com.mezon.classmanagement.backend.domain_document.main.moderation.extractor;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.boot.json.JsonWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//MultipartFile -> file.getInputStream() -> XWPFDocument -> StringBuilder -> String
@Component
public class DocxTextExtractor implements FileTextExtractor {

    //Docx -> apache POI -> String
    @Override
    public String extract(MultipartFile file) throws IOException {
        StringBuilder text = new StringBuilder();

        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String paragraphText = paragraph.getText();

                if (paragraphText != null &&  !paragraphText.isBlank()) {
                    text.append(paragraphText);
                    text.append("\n");
                }
            }
        }

        return text.toString();
    }
}
