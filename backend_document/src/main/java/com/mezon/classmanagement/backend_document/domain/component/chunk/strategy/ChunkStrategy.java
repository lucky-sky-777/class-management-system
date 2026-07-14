package com.mezon.classmanagement.backend_document.domain.component.chunk.strategy;

import java.util.List;

public interface ChunkStrategy {

	List<String> getChunkList(String filePath);

}