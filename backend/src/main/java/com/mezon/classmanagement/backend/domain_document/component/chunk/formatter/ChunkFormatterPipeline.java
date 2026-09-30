package com.mezon.classmanagement.backend.domain_document.component.chunk.formatter;

import dev.langchain4j.data.document.Metadata;

import java.util.ArrayList;
import java.util.List;

public class ChunkFormatterPipeline extends ChunkFormatter {

	private final List<ChunkFormatter> formatters;

	public ChunkFormatterPipeline(List<ChunkFormatter> formatters) {
		this.formatters = formatters;
	}

	@Override
	public String format(String chunk, Metadata metadata) {
		String result = chunk;

		for (ChunkFormatter formatter : formatters) {
			result = formatter.format(result, metadata);
		}

		return result;
	}

}