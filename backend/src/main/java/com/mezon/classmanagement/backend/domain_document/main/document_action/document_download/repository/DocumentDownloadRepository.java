package com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.repository;

import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.entity.DocumentDownload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentDownloadRepository extends JpaRepository<DocumentDownload, Long> {

	@Query("""
	SELECT dd
	FROM DocumentDownload dd
	JOIN FETCH dd.document
	JOIN FETCH dd.creator
	WHERE dd.document.id = :documentId
	AND dd.creator.id = :creatorUserId
	""")
	Optional<DocumentDownload> findByDocument_IdAndCreator_Id(Long documentId, Long creatorUserId);

	@Query("""
	SELECT dd
	FROM DocumentDownload dd
	JOIN FETCH dd.document
	JOIN FETCH dd.creator
	WHERE dd.creator.id = :creatorUserId
	ORDER BY dd.createdAt DESC
	""")
	List<DocumentDownload> findAllByCreator_IdOrderByCreatedAtDesc(Long creatorId);

	@Query("""
    SELECT d
    FROM DocumentDownload d
    WHERE d.document.id = :documentId
        AND EXISTS (
            SELECT dd.id
            FROM DirectoryDocument dd
            JOIN dd.directory directory
            WHERE dd.document.id = d.document.id
                AND directory.clazz.privacy = 'PUBLIC'
        )
    """)
	List<DocumentDownload> findAllByDocument_IdAndClassIsPublic(
			@Param("documentId") Long documentId
	);

}