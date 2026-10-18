package com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter;

import dev.langchain4j.data.document.DocumentSplitter;

public interface DocumentSplitterStrategy {

	String getName();

	DocumentSplitter getSplitter();

}