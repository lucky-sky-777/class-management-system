package com.mezon.classmanagement.backend_document.domain.component.split.strategy.impl;

import com.mezon.classmanagement.backend_document.config.SplitConfig;
import com.mezon.classmanagement.backend_document.domain.component.split.strategy.SplitStrategy;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByWordSplitter;
import org.springframework.stereotype.Component;

@Component(value = SplitByWordStrategy.NAME)
public class SplitByWordStrategy implements SplitStrategy {

	public static final String NAME = "splitByWordStrategy";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public DocumentSplitter getSplitter() {
		return new DocumentByWordSplitter(
				SplitConfig.MAX_SEGMENT_SIZE,
				SplitConfig.MAX_OVERLAP_SIZE,
				SplitConfig.TOKEN_COUNT_ESTIMATOR
		);
	}

}