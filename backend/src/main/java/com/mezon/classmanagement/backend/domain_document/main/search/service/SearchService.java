package com.mezon.classmanagement.backend.domain_document.main.search.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.repository.DocumentChunkRepository;
import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.BaseVector;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.repository.DocumentRepository;
import com.mezon.classmanagement.backend.domain_document.main.search.repository.SearchRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain_document.main.search.dto.response.SearchResultDtoProjection;
import com.mezon.classmanagement.backend.domain_document.main.search.repository.SearchRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import java.util.List;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class SearchService {

	EmbeddingService embeddingService;
	DocumentChunkRepository documentChunkRepository;


	SearchRepository searchRepository;

	/*
	 * Chức năng tìm kiếm viết ở đây
	 */

	// ================= TÌM KIẾM CHO NGƯỜI DÙNG =================

	private static final int MAX_QUERY_LENGTH = 200;
	private static final int DEFAULT_LIMIT = 10;
	private static final int MAX_LIMIT = 50;
	private static final int CANDIDATE_FACTOR = 3;
	private static final double DEFAULT_MIN_SCORE = 0.65;

	public List<SearchResultDtoProjection> searchForUser(
			Long userId,
			String q,
			Long classId,
			Long directoryId,
			Long documentId,
			Integer limit,
			Double minSimilarity
	) {
		String query = (q == null) ? "" : q.trim();

		if (query.isEmpty()) {
			throw new GlobalException(
					GlobalException.Type.INVALID_REQUEST,
					"Từ khóa tìm kiếm không được để trống"
			);
		}
		if (query.length() > MAX_QUERY_LENGTH) {
			throw new GlobalException(
					GlobalException.Type.INVALID_REQUEST,
					"Từ khóa tối đa " + MAX_QUERY_LENGTH + " ký tự"
			);
		}

		int size = (limit == null) ? DEFAULT_LIMIT : limit;
		if (size < 1 || size > MAX_LIMIT) {
			throw new GlobalException(
					GlobalException.Type.INVALID_REQUEST,
					"limit phải trong khoảng 1 - " + MAX_LIMIT
			);
		}

		double minScore = (minSimilarity == null) ? DEFAULT_MIN_SCORE : minSimilarity;
		if (minScore < 0 || minScore > 1) {
			throw new GlobalException(
					GlobalException.Type.INVALID_REQUEST,
					"minSimilarity phải trong khoảng 0 - 1"
			);
		}

		// gọi Gemini đúng 1 lần cho cả request
		float[] embedding = embeddingService.embedSingleQueryText(query).getValues();

		// lấy dư để sau khi gom theo tài liệu vẫn đủ `size`
		List<SearchResultDtoProjection> rows = searchRepository.searchForUser(
				userId,
				classId,
				directoryId,
				documentId,
				embedding,
				size * CANDIDATE_FACTOR,
				minScore
		);

		// SQL đã ORDER BY score DESC -> dòng đầu tiên của mỗi tài liệu là đoạn khớp nhất
		Set<Long> seen = new HashSet<>();
		List<SearchResultDtoProjection> result = new ArrayList<>();

		for (SearchResultDtoProjection row : rows) {
			if (seen.add(row.getId())) {
				result.add(row);
				if (result.size() >= size) break;
			}
		}

		return result;
	}

	//////////

	/*
	 * Mấy hàm ở dưới dùng cho RAG
	 */

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