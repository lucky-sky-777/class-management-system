package com.mezon.classmanagement.backend_document.domain.component.chunk.strategy.impl;

import com.mezon.classmanagement.backend_document.domain.component.chunk.strategy.ChunkStrategy;
import com.mezon.classmanagement.backend_document.domain.component.split.strategy.impl.SplitByParagraphStrategy;
import com.mezon.classmanagement.backend_document.domain.component.chunk.service.ChunkService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component(value = ChunkByParagraphStrategy.NAME)
public class ChunkByParagraphStrategy implements ChunkStrategy {

	public static final String NAME = "chunkByParagraphStrategy";

	ChunkService chunkService;
	SplitByParagraphStrategy splitByParagraphStrategy;

	@Override
	public List<String> getChunkList(String filePath) {
		return chunkService.getChunkListFromFilePath(
				filePath,
				splitByParagraphStrategy
		);
	}

}