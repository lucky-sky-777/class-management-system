package com.mezon.classmanagement.backend.domain_document.component.document_source.impl;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import dev.langchain4j.data.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class FileDocumentSource extends DocumentSource {

	public FileDocumentSource(Object source) {
		super(source);
	}

	@Override
	public Document toDocument() throws Exception {
		return FileUtils.toDocument((File) getSource());
	}

}