package com.mezon.classmanagement.backend.domain_document.component.chunk.strategy;

import java.util.List;

public interface ChunkStrategy {

	List<String> getChunkList(String filePath);

}