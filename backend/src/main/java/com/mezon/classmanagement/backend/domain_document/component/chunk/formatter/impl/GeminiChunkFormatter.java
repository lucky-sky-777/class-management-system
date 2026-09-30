package com.mezon.classmanagement.backend.domain_document.component.chunk.formatter.impl;

import com.mezon.classmanagement.backend.domain_document.component.chunk.builder.ChunkListBuilder;
import com.mezon.classmanagement.backend.domain_document.component.chunk.formatter.ChunkFormatter;
import dev.langchain4j.data.document.Metadata;

public class GeminiChunkFormatter extends ChunkFormatter {

	@Override
	public String format(
			String chunk,
			Metadata metadata
	) {
		return ChunkListBuilder.formatChunkForGemini(chunk, metadata);
	}

}