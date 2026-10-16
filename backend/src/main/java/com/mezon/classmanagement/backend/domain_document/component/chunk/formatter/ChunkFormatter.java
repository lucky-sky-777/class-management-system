package com.mezon.classmanagement.backend.domain_document.component.chunk.formatter;

import dev.langchain4j.data.document.Metadata;

import java.util.ArrayList;
import java.util.List;

public abstract class ChunkFormatter {

	public List<String> formatList(List<String> chunkList, Metadata metadata) {
		List<String> formattedChunkList = new ArrayList<>(chunkList.size());

		int chunkIndex = 1;
		for (String chunk : chunkList) {
			formattedChunkList.add(format(chunk, metadata.put("chunkIndex", chunkIndex++)));
		}

		return formattedChunkList;
	}

	protected abstract String format(String chunk, Metadata metadata);

}