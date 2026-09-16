package com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph;

import com.mezon.classmanagement.backend.config.SplitConfig;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.SplitStrategy;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import org.springframework.stereotype.Component;

@Component(value = SplitByParagraphWithoutOverlapStrategy.NAME)
public class SplitByParagraphWithoutOverlapStrategy implements SplitStrategy {

	public static final String NAME = "splitByParagraphWithoutOverlapStrategy";

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