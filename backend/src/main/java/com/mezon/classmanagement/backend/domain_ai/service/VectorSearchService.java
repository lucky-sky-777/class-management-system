package com.mezon.classmanagement.backend.domain_ai.service;


import com.mezon.classmanagement.backend.domain_ai.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_ai.entity.Vector3072;
import com.mezon.classmanagement.backend.domain_ai.repository.DocumentChunkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VectorSearchService {

	private final EmbeddingService embeddingService;

	private final DocumentChunkRepository chunkRepository;

	public List<DocumentChunk> search(
			Long classId,
			String question
	) {

		Vector3072 questionEmbedding = null;//embeddingService.embed(question);

		String vectorString =
				embeddingService
						.toVectorString(
								questionEmbedding
						);

		return chunkRepository.searchSimilarChunks(
				classId,
				vectorString,
				5
		);
	}
}