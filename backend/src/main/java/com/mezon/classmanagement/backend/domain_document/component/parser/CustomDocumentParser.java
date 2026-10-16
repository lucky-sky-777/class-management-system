package com.mezon.classmanagement.backend.domain_document.component.parser;

import dev.langchain4j.data.document.Document;
import org.apache.tika.metadata.Metadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface CustomDocumentParser {

	//String extractToFile(InputStream inputStream, String outputFileName) throws Exception;

	Document parse(InputStream inputStream) throws Exception;

}