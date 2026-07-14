package com.mezon.classmanagement.backend_document.domain.component.split.strategy;

import dev.langchain4j.data.document.DocumentSplitter;

public interface SplitStrategy {

	String getName();

	DocumentSplitter getSplitter();

}