package com.mezon.classmanagement.backend.domain_document.main.moderation.extractor;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//MultipartFile -> extarct() -> string
public interface FileTextExtractor {
    String extract(MultipartFile file) throws IOException;
}
