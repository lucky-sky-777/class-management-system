package com.mezon.classmanagement.backend.domain_document.component.document_metadata.repository;

import com.mezon.classmanagement.backend.domain_document.component.document_metadata.entity.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentMetadataRepository extends JpaRepository<DocumentMetadata, Long> {
    Optional<DocumentMetadata> findByDocument_Id(Long document_Id) ;
}
