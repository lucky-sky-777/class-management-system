package com.mezon.classmanagement.backend.domain_document.main.document.repository;

import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentCreatorUserIdResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByKey(String key);

	@Query(value = """
		SELECT id, embedding
		FROM documents
	""", nativeQuery = true)
	List<Document> getAll();

	@Query(value = """
    SELECT d.*
    FROM documents d
    WHERE d.id IN (
        SELECT dd.document_id
        FROM directory_documents dd
        WHERE dd.directory_id = :directoryId
    )
    order by d.created_at desc
    """, nativeQuery = true)
	List<Document> findByDirectoryId(
			@Param("directoryId") Long directoryId
	);

	@Query("""
    SELECT DISTINCT dd.document.id
    FROM DirectoryDocument dd
    JOIN dd.directory directory
    JOIN dd.document document
    WHERE document.creator.id = :creatorUserId
        AND directory.clazz.id = :classId
        AND directory.id = :directoryId
    """)
	List<Long> getDocumentIdListByCreatorAndClassAndDirectory(
			@Param("creatorUserId") Long creatorUserId,
			@Param("classId") Long classId,
			@Param("directoryId") Long directoryId
	);

	@Query("""
	select d.key
	from Document d
	where d.id in :documentIdList
	""")
	List<String> getDocumentKeyListByDocumentIdList(
			@Param("documentIdList") List<Long> documentIdList
	);

}