package com.mezon.classmanagement.backend.domain_document.main.search.service;

import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.repository.DocumentChunkRepository;
import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.BaseVector;
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

	DocumentChunkRepository documentChunkRepository;

	public enum SearchRange {
		SYSTEM,
		CLASS
	}

//	public List<DocumentChunk> search(String query, SearchRange searchRange, Long... classId) {
//		return switch (searchRange) {
//			case SYSTEM -> searchAll(query);
//			case CLASS -> searchClass(classId[0], query, 5);
//		};
//	}

	public List<DocumentChunk> searchAll(
			String query
	) {
		BaseVector queryVector = embeddingService.embedSingleQueryText(query);
		float[] queryEmbedding = queryVector.getValues();

		return searchAll(queryEmbedding);
	}

	public List<DocumentChunk> searchAll(
			float[] queryEmbedding
	) {
		return searchAll(queryEmbedding, 5);
	}

	public List<DocumentChunk> searchAll(
			float[] queryEmbedding,
			int limit
	) {
		return documentChunkRepository.searchInAllSystemDocument(
				queryEmbedding,
				limit
		);
	}

	public List<DocumentChunk> searchClass(Long classId, String query, int limit) {
		BaseVector queryVector = embeddingService.embedSingleQueryText(query);
		float[] queryEmbedding = queryVector.getValues();
		return documentChunkRepository.searchInAllClassDocument(classId, queryEmbedding, limit);
	}

//	public List<DocumentChunk> searchDocument() {
//		//
//	}

}