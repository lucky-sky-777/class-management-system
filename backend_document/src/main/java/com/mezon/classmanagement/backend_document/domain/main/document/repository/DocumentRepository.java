package com.mezon.classmanagement.backend_document.domain.main.document.repository;

import com.mezon.classmanagement.backend_document.domain.main.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}