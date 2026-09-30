package com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.config.SplitConfig;
import com.mezon.classmanagement.backend.domain_document.component.chunk.builder.ChunkListBuilder;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.split.DocumentSplitterService;
import com.mezon.classmanagement.backend.domain_document.component.split.service.SplitService;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.SplitStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.impl.DocumentSplitterByParagraphWithoutOverlapStrategy;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component(value = SplitByParagraphWithoutOverlapStrategy.NAME)
public class SplitByParagraphWithoutOverlapStrategy implements SplitStrategy {

	DocumentSplitterService documentSplitterService;

	DocumentSplitterByParagraphWithoutOverlapStrategy documentSplitterByParagraphWithoutOverlapStrategy;

	public static final String NAME = "splitByParagraphWithoutOverlapStrategy";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<String> getTextList(DocumentSource documentSource) throws Exception {
		return ChunkListBuilder.getChunkList(
				documentSource.toDocument(),
				documentSplitterService.getSplitter(documentSplitterByParagraphWithoutOverlapStrategy)
		);
//		return new ChunkListBuilder()
//				.from(documentSource)
//				.documentSplitter(documentSplitterService.getSplitter(documentSplitterByParagraphWithoutOverlapStrategy))
//				.forGemini()
//				.build();
	}

}