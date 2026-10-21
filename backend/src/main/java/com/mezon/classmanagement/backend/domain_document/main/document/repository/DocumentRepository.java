package com.mezon.classmanagement.backend.domain_document.main.document.repository;

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
	select *
	from documents d
	where d.id in (
		select dd.id
		from directory_documents dd
		where dd.directory_id = :directoryId
	)
	""", nativeQuery = true)
	List<Document> findByDirectoryId(@Param("directoryId") Long directoryId);

//	@Query("""
//
//	""")
//	List<DocumentResponseDto> getByDirectoryId(Long directoryId);

}