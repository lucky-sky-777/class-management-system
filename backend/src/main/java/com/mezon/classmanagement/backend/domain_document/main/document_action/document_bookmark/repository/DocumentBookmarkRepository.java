package com.mezon.classmanagement.backend.domain_document.main.document_bookmark.repository;

import com.mezon.classmanagement.backend.domain_document.main.document_bookmark.entity.DocumentBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentBookmarkRepository extends JpaRepository<DocumentBookmark, Long> {

	boolean existsByDocument_IdAndCreator_Id(
			Long documentId,
			Long creatorId
	);

	Optional<DocumentBookmark> findByDocument_IdAndCreator_Id(Long documentId, Long creatorUserId);

	List<DocumentBookmark> findAllByCreator_IdOrderByCreatedAtDesc(Long creatorId);

	@Query("""
    SELECT db
    FROM DocumentBookmark db
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