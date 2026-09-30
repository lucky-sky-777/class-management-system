package com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.impl;

import com.mezon.classmanagement.backend.domain_document.component.chunk.builder.ChunkListBuilder;
import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.ChunkStrategy;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.split.service.SplitService;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphWithoutOverlapStrategy;
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

	SplitService splitService;
	SplitByParagraphWithoutOverlapStrategy splitByParagraphWithoutOverlapStrategy;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<String> format(List<String> textList) {
		return List.of();
	}

	@Override
	public List<String> getChunkList(DocumentSource documentSource) throws Exception {
		return splitService.getChunkList(
				splitByParagraphWithoutOverlapStrategy,
				documentSource
		);
	}

}