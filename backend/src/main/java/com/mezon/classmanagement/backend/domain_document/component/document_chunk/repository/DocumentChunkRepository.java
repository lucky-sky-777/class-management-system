package com.mezon.classmanagement.backend.domain_document.component.document_chunk.repository;

import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

	@Query(value = """
	SELECT *
	FROM document_chunks
	WHERE document_id IN (
		SELECT id
		FROM documents
		WHERE class_id = :classId
	)
	ORDER BY embedding <=> CAST(:embedding AS vector)
	LIMIT :limit
	""", nativeQuery = true
	)
	List<DocumentChunk> searchSimilarChunks(
			@Param("classId") Long classId,
			@Param("embedding") String embedding,
			@Param("limit") int limit
	);

	@Query("""
	SELECT c
	FROM DocumentChunk c
	ORDER BY cosine_distance(c.embedding, :embedding)
	""")
	List<DocumentChunk> search(
			@Param("embedding") float[] embedding
	);

}