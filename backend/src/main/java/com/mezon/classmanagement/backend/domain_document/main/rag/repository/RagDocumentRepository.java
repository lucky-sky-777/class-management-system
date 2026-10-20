package com.mezon.classmanagement.backend.domain_document.main.rag.repository;

import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RagDocumentRepository extends JpaRepository<Document, Long> {
}
