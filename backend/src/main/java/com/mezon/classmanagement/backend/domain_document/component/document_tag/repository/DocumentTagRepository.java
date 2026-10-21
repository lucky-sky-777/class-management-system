package com.mezon.classmanagement.backend.domain_document.component.document_tag.repository;

import com.mezon.classmanagement.backend.domain_document.component.document_tag.entity.DocumentTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentTagRepository extends JpaRepository<DocumentTag, Long> {
    Optional<DocumentTag> findByDocument_Id(Long documentId);
}
