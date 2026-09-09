package com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl;

import com.mezon.classmanagement.backend.config.SplitConfig;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.SplitStrategy;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import org.springframework.stereotype.Component;

@Component(value = SplitByAllStrategy.NAME)
public class SplitByAllStrategy implements SplitStrategy {

	protected static final String NAME = "splitByTokenStrategy";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public DocumentSplitter getSplitter() {
		return DocumentSplitters.recursive(
				SplitConfig.MAX_SEGMENT_SIZE,
				SplitConfig.MAX_OVERLAP_SIZE,
				SplitConfig.TOKEN_COUNT_ESTIMATOR
		);
	}

}