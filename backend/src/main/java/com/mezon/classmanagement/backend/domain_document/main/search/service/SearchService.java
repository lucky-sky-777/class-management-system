package com.mezon.classmanagement.backend.domain_document.main.search.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.repository.DocumentChunkRepository;
import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.BaseVector;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class SearchService {

	EmbeddingService embeddingService;
	DocumentChunkRepository documentChunkRepository;

	/**
	 * Tìm trong tất cả tài liệu trên hệ thống
	 *
	 * @param query Nội dung tìm kiếm
	 * @param limit Giới hạn tìm kiếm (số chunk tối đa)
	 * @return Danh sách chunk
	 */
	public List<DocumentChunk> searchAll(
			String query,
			int limit
	) {
		BaseVector queryVector = embeddingService.embedSingleQueryText(query);
		float[] queryEmbedding = queryVector.getValues();
		return searchAll(queryEmbedding, limit);
	}

	public List<DocumentChunk> searchAll(
			float[] queryEmbedding,
			int limit
	) {
		return documentChunkRepository.searchInAllSystemDocument(queryEmbedding, limit);
	}

	/**
	 * Tìm trong tất cả Tài liệu của một Lớp
	 *
	 * @param classId ID lớp
	 * @param query   Nội dung tìm kiếm
	 * @param limit   Giới hạn tìm kiếm (số chunk tối đa)
	 * @return Danh sách chunk
	 */
	public List<DocumentChunk> searchClass(
			Long classId,
			String query,
			int limit
	) {
		BaseVector queryVector = embeddingService.embedSingleQueryText(query);
		float[] queryEmbedding = queryVector.getValues();
		return searchClass(classId, queryEmbedding, limit);
	}

	public List<DocumentChunk> searchClass(
			Long classId,
			float[] queryEmbedding,
			int limit
	) {
		return documentChunkRepository.searchInAllClassDocument(classId, queryEmbedding, limit);
	}

	/**
	 * Tìm trong một Tài liệu cụ thể
	 *
	 * @param documentId ID tài liệu
	 * @param query      Nội dung tìm kiếm
	 * @param limit      Giới hạn tìm kiếm (số chunk tối đa)
	 * @return Danh sách chunk
	 */
	public List<DocumentChunk> searchDocument(
			Long documentId,
			String query,
			int limit
	) {
		BaseVector queryVector = embeddingService.embedSingleQueryText(query);
		float[] queryEmbedding = queryVector.getValues();
		return searchDocument(documentId, queryEmbedding, limit);
	}

	public List<DocumentChunk> searchDocument(
			Long documentId,
			float[] queryEmbedding,
			int limit
	) {
		return documentChunkRepository.searchInOneDocument(documentId, queryEmbedding, limit);
	}

}