package com.mezon.classmanagement.backend.domain_document.main.moderation.extractor;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PdfTextExtractor implements FileTextExtractor {

    @Override
    public String extract(MultipartFile file) throws IOException {
        //tốn ram
        //byte[] bytes = file.getBytes();

        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(file.getInputStream()))) {
            PDFTextStripper stripper = new PDFTextStripper();

            return stripper.getText(document);

        }
    }
}
