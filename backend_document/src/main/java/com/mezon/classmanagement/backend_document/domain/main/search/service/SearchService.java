package com.mezon.classmanagement.backend_document.domain.main.search.service;

import com.mezon.classmanagement.backend_document.domain.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend_document.domain.component.document_chunk.repository.DocumentChunkRepository;
import com.mezon.classmanagement.backend_document.domain.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend_document.domain.component.vector.entity.impl.Vector3072;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class SearchService {

	EmbeddingService embeddingService;

	DocumentChunkRepository chunkRepository;

	public List<DocumentChunk> search(
			Long classId,
			String query
	) throws Exception {
		Vector3072 queryEmbedding = embeddingService.embedSingleQueryText(query);

		String vectorString =
				embeddingService
						.toVectorString(
								queryEmbedding
						);

		return chunkRepository.searchSimilarChunks(
				classId,
				vectorString,
				5
		);
	}

}