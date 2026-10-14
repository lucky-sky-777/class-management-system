package com.mezon.classmanagement.backend.domain_document.component.document_source.impl;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import dev.langchain4j.data.document.Document;

import java.io.File;
import java.io.InputStream;

public class InputStreamDocumentSource extends DocumentSource {

	public InputStreamDocumentSource(Object source) {
		super(source);
	}

	@Override
	public Document toDocument() throws Exception {
		return FileUtils.toDocument((InputStream) getSource());
	}

}