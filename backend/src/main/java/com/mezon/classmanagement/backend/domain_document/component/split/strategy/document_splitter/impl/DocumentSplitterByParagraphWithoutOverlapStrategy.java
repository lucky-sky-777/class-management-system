package com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.impl;

import com.mezon.classmanagement.backend.config.SplitConfig;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.DocumentSplitterStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphStrategy;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import org.springframework.stereotype.Component;

@Component(value = DocumentSplitterByParagraphWithoutOverlapStrategy.NAME)
public class DocumentSplitterByParagraphWithoutOverlapStrategy implements DocumentSplitterStrategy {

	public static final String NAME = "documentSplitterByParagraphWithoutOverlapStrategy";


	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public DocumentSplitter getSplitter() {
		return new DocumentByParagraphSplitter(
				SplitConfig.MAX_SEGMENT_SIZE,
				0
		);
	}

}