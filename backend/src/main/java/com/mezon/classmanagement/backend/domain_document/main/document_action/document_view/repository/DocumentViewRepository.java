package com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.repository;

import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.entity.DocumentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentViewRepository extends JpaRepository<DocumentView, Long> {

	@Query("""
	SELECT dv
	FROM DocumentView dv
	JOIN FETCH dv.document
	JOIN FETCH dv.creator
	WHERE dv.document.id = :documentId
	AND dv.creator.id = :creatorUserId
	""")
	Optional<DocumentView> findByDocument_IdAndCreator_Id(Long documentId, Long creatorUserId);

	@Query("""
	SELECT dv
	FROM DocumentView dv
	JOIN FETCH dv.document
	JOIN FETCH dv.creator
	WHERE dv.creator.id = :creatorUserId
	ORDER BY dv.createdAt DESC
	""")
	List<DocumentView> findAllByCreator_IdOrderByCreatedAtDesc(Long creatorUserId);

	@Query("""
    SELECT dv
    FROM DocumentView dv
    WHERE dv.document.id = :documentId
        AND EXISTS (
            SELECT dd.id
            FROM DirectoryDocument dd
            JOIN dd.directory directory
            WHERE dd.document.id = dv.document.id
                AND directory.clazz.privacy = 'PUBLIC'
        )
    """)
	List<DocumentView> findAllByDocument_IdAndClassIsPublic(
			@Param("documentId") Long documentId
	);

}