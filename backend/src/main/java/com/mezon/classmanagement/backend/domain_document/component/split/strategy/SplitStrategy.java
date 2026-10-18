package com.mezon.classmanagement.backend.domain_document.component.split.strategy;

import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;

import java.util.List;

public interface SplitStrategy {

	String getName();

	//DocumentSplitter getSplitter();
	List<String> getTextList(DocumentSource documentSource) throws Exception;

}