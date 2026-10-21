package com.mezon.classmanagement.backend.domain_document.main.document.repository;

import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

	Optional<Document> findByKey(String key);

	@Query(value = """
		SELECT id, embedding
		FROM documents
	""", nativeQuery = true)
	List<Document> getAll();

}