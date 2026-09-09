package com.mezon.classmanagement.backend.domain_document.component.split.strategy;

import dev.langchain4j.data.document.DocumentSplitter;

public interface SplitStrategy {

	String getName();

	DocumentSplitter getSplitter();

}