package com.mezon.classmanagement.backend.domain_document.component.document_chunk.repository;

import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

	@Query(value = """
	SELECT *
	FROM document_chunks dc
	ORDER BY dc.embedding <=> CAST(:embedding AS vector) ASC
	LIMIT :limit
	""", nativeQuery = true
	)
	List<DocumentChunk> searchInAllSystemDocument(
			float[] embedding,
			int limit
	);

	@Query(value = """
    SELECT dc.*
    FROM document_chunks dc
    WHERE EXISTS (
        SELECT 1
        FROM directory_documents dd
        JOIN directories d
        	ON d.id = dd.directory_id
        WHERE dd.document_id = dc.document_id
        	AND d.class_id = :classId
    )
    ORDER BY dc.embedding <=> CAST(:embedding AS vector) ASC
    LIMIT :limit
    """, nativeQuery = true)
	List<DocumentChunk> searchInAllClassDocument(
			@Param("classId") Long classId,
			@Param("embedding") float[] embedding,
			@Param("limit") int limit
	);

	@Query(value = """
	SELECT *
	FROM document_chunks dc
	WHERE dc.document_id = :documentId
	ORDER BY dc.embedding <=> CAST(:embedding AS vector) ASC
	LIMIT :limit
	""", nativeQuery = true
	)
	List<DocumentChunk> searchInOneDocument(
			@Param("documentId") Long documentId,
			@Param("embedding") float[] embedding,
			@Param("limit") int limit
	);

	List<DocumentChunk> findByDocument_IdOrderByIndexAsc(Long documentId);
}