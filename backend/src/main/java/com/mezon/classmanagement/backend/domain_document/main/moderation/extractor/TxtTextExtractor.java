package com.mezon.classmanagement.backend.domain_document.main.moderation.extractor;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//TxtTextExtractor implemets FileTextExtract
@Component
public class TxtTextExtractor implements FileTextExtractor{

    @Override
    public String extract(MultipartFile file) throws IOException {
        return new String(
                file.getBytes(),
                StandardCharsets.UTF_8
        );
    }
}
