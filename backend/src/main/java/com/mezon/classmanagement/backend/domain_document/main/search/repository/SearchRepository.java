package com.mezon.classmanagement.backend.domain_document.main.search.repository;

import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.main.search.dto.response.SearchResultDtoProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchRepository extends Repository<DocumentChunk, Long> {

    @Query(value = """
	SELECT
	    s.document_id      AS "id",
	    doc.title          AS "title",
	    doc.description    AS "description",
	    doc.file_name      AS "fileName",

	    (SELECT COUNT(*) FROM document_downloads dl
	      WHERE dl.document_id = s.document_id) AS "downloadCount",

	    EXISTS (
	        SELECT 1 FROM document_bookmarks bm
	        WHERE bm.document_id = s.document_id
	          AND bm.creator_user_id = :userId
	    ) AS "isBookmarked",

	    u.id           AS "creatorUserId",
	    u.display_name AS "creatorDisplayName",
	    u.avatar_url   AS "creatorAvatarUrl",
	    doc.created_at AS "createdAt",

	    s.class_id       AS "classId",
	    s.class_name     AS "className",
	    s.directory_id   AS "directoryId",
	    s.directory_name AS "directoryName",
	    s.chunk_index    AS "chunkIndex",
	    s.chunk_text     AS "chunkText",
	    s.score          AS "score",
	    'semantic'       AS "matchType"
	FROM (
	    SELECT
	        dc.document_id,
	        dc.index   AS chunk_index,
	        dc.content AS chunk_text,
	        dir.id     AS directory_id,
	        dir.name   AS directory_name,
	        cls.id     AS class_id,
	        cls.name   AS class_name,
	        1 - (dc.embedding <=> CAST(:embedding AS vector)) AS score
	    FROM document_chunks dc
	    JOIN documents dstat        ON dstat.id = dc.document_id
	                               AND dstat.status = 'UPLOADED'
	    JOIN directory_documents dd ON dd.document_id = dc.document_id
	    JOIN directories dir        ON dir.id = dd.directory_id
	    JOIN classes cls            ON cls.id = dir.class_id
	                               AND (
	                                   cls.privacy = 'PUBLIC'
	                                   OR EXISTS (
	                                       SELECT 1 FROM class_users cu
	                                       WHERE cu.class_id = cls.id
	                                         AND cu.user_id = :userId
	                                   )
	                               )
	    WHERE
	        (CAST(:classId AS bigint)         IS NULL OR cls.id = :classId)
	        AND (CAST(:directoryId AS bigint) IS NULL OR dir.id = :directoryId)
	        AND (CAST(:documentId AS bigint)  IS NULL OR dc.document_id = :documentId)
		    AND LENGTH(TRIM(dc.content)) >= 50
	    ORDER BY dc.embedding <=> CAST(:embedding AS vector) ASC
	    LIMIT :candidateLimit
	) s
	JOIN documents doc ON doc.id = s.document_id
	JOIN users u       ON u.id = doc.creator_user_id
	WHERE s.score >= :minScore
	ORDER BY s.score DESC
	""", nativeQuery = true)
    List<SearchResultDtoProjection> searchForUser(
            @Param("userId") Long userId,
            @Param("classId") Long classId,
            @Param("directoryId") Long directoryId,
            @Param("documentId") Long documentId,
            @Param("embedding") float[] embedding,
			@Param("candidateLimit") int candidateLimit,
            @Param("minScore") double minScore
    );


}
