package com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.impl;

import com.mezon.classmanagement.backend.config.SplitConfig;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.DocumentSplitterStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphStrategy;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import org.springframework.stereotype.Component;

@Component(value = DocumentSplitterByParagraphWithOverlapStrategy.NAME)
public class DocumentSplitterByParagraphWithOverlapStrategy implements DocumentSplitterStrategy {

	public static final String NAME = "documentSplitterByParagraphWithOverlapStrategy";


	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public DocumentSplitter getSplitter() {
		return new DocumentByParagraphSplitter(
				SplitConfig.MAX_SEGMENT_SIZE,
				SplitConfig.MAX_OVERLAP_SIZE,
				SplitConfig.TOKEN_COUNT_ESTIMATOR
		);
	}

}