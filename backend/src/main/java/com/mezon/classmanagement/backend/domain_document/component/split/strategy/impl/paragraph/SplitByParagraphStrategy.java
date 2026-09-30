package com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph;

import com.mezon.classmanagement.backend.config.SplitConfig;
import com.mezon.classmanagement.backend.domain_document.component.chunk.builder.ChunkListBuilder;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.split.DocumentSplitterService;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.SplitStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.impl.DocumentSplitterByParagraphWithOverlapStrategy;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(value = SplitByParagraphStrategy.NAME)
public class SplitByParagraphStrategy implements SplitStrategy {

	DocumentSplitterService documentSplitterService;

	DocumentSplitterByParagraphWithOverlapStrategy documentSplitterByParagraphWithOverlapStrategy;

	public static final String NAME = "splitByParagraphStrategy";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<String> getTextList(DocumentSource documentSource) throws Exception {
		return ChunkListBuilder.getChunkList(
				documentSource.toDocument(),
				documentSplitterService.getSplitter(documentSplitterByParagraphWithOverlapStrategy)
		);
	}

}