package com.mezon.classmanagement.backend.domain_document.component.document_source.impl;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import dev.langchain4j.data.document.Document;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileDocumentSource extends DocumentSource {

	public MultipartFileDocumentSource(Object source) {
		super(source);
	}

	@Override
	public Document toDocument() throws Exception {
		return FileUtils.toDocument((MultipartFile) getSource());
	}

}