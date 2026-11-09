package com.mezon.classmanagement.backend.domain_document.main.document_action.document_bookmark.repository;

import com.mezon.classmanagement.backend.domain_document.main.document_action.document_bookmark.entity.DocumentBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentBookmarkRepository extends JpaRepository<DocumentBookmark, Long> {

	@Query("""
	SELECT db
	FROM DocumentBookmark db
	JOIN FETCH db.document
	JOIN FETCH db.creator
	WHERE db.document.id = :documentId
	AND db.creator.id = :creatorUserId
	""")
	Optional<DocumentBookmark> findByDocument_IdAndCreator_Id(Long documentId, Long creatorUserId);

	@Query("""
	SELECT db
	FROM DocumentBookmark db
	JOIN FETCH db.document
	JOIN FETCH db.creator
	WHERE db.creator.id = :creatorUserId
	ORDER BY db.createdAt DESC
	""")
	List<DocumentBookmark> findAllByCreator_IdOrderByCreatedAtDesc(Long creatorId);

	@Query("""
    SELECT db
    FROM DocumentBookmark db
    JOIN FETCH db.document
    JOIN FETCH db.creator
    WHERE db.document.id = :documentId
        AND EXISTS (
            SELECT dd.id
            FROM DirectoryDocument dd
            JOIN dd.directory directory
            WHERE dd.document.id = db.document.id
                AND directory.clazz.privacy = 'PUBLIC'
        )
    """)
	List<DocumentBookmark> findAllByDocument_IdAndClassIsPublic(
			@Param("documentId") Long documentId
	);

}