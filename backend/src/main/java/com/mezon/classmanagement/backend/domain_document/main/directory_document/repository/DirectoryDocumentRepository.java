package com.mezon.classmanagement.backend.domain_document.main.directory_document.repository;

import com.mezon.classmanagement.backend.domain_document.main.directory_document.entity.DirectoryDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectoryDocumentRepository extends JpaRepository<DirectoryDocument, Long> {

	boolean existsByDirectoryIdAndDocumentId(Long directoryId, Long documentId);

}