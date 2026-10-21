package com.mezon.classmanagement.backend.domain_document.main.document.repository;

import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentCreatorUserIdResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
	select new com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentCreatorUserIdResponseDto(
		d.id,
		d.creator.id
	)
	from Document d
	where d.id in (
		:documentIdList
	)
	""")
	List<DocumentCreatorUserIdResponseDto> getCreatorUserIdListByDocumentIdList(
			@Param("documentIdList") List<Long> dcocumentIdList
	);

}