package com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.impl;

import com.mezon.classmanagement.backend.domain_document.component.chunk.builder.ChunkListBuilder;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.ChunkStrategy;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.split.DocumentSplitterService;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.impl.DocumentSplitterByParagraphWithoutOverlapStrategy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component(value = ChunkByParagraphWithoutOverlapStrategy.NAME)
public class ChunkByParagraphWithoutOverlapStrategy implements ChunkStrategy {

	public static final String NAME = "chunkByParagraphWithoutOverlapStrategy";

	DocumentSplitterService documentSplitterService;
	DocumentSplitterByParagraphWithoutOverlapStrategy
			documentSplitterByParagraphWithoutOverlapStrategy;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<String> getChunkList(DocumentSource documentSource) throws Exception {
		return ChunkListBuilder.getChunkList(
				documentSource.toDocument(),
				documentSplitterService
						.getSplitter(
								documentSplitterByParagraphWithoutOverlapStrategy
						)
		);
	}

}