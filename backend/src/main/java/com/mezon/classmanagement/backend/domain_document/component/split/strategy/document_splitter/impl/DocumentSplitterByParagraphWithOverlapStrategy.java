package com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.impl;

import com.mezon.classmanagement.backend.config.SplitConfig;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.DocumentSplitterStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.custom.VnCoreNLPDocumentBySentenceSplitter;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.pipeline.VnCoreNLP;

@RequiredArgsConstructor
@Component(value = DocumentSplitterByParagraphWithOverlapStrategy.NAME)
public class DocumentSplitterByParagraphWithOverlapStrategy implements DocumentSplitterStrategy {

	private final VnCoreNLP vnCoreNLP;

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
				SplitConfig.TOKEN_COUNT_ESTIMATOR,
				new VnCoreNLPDocumentBySentenceSplitter(
						SplitConfig.MAX_SEGMENT_SIZE,
						SplitConfig.MAX_OVERLAP_SIZE,
						SplitConfig.TOKEN_COUNT_ESTIMATOR,
						vnCoreNLP
				)
		);
	}

}