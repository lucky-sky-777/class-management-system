package com.mezon.classmanagement.backend.domain_document.component.document_metadata.service;

import com.mezon.classmanagement.backend.domain_document.component.document_metadata.entity.DocumentMetadata;
import com.mezon.classmanagement.backend.domain_document.component.document_metadata.repository.DocumentMetadataRepository;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.repository.DocumentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain_document.component.s3.service.S3Service;
import java.io.InputStream;
import java.util.HashMap;

import java.util.Map;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DocumentMetadataService {
    DocumentMetadataRepository  documentMetadataRepository;
    DocumentRepository  documentRepository;

    S3Service s3Service;

    @Transactional
    public void saveMetadata(Long documentId, Map<String, Object> metadata){
        Document document = documentRepository
                .findById(documentId)
                .orElse(null);

        if(document == null){
            System.out.println("Skip metadata. Document not found: " + documentId);
            return;
        }

        Optional<DocumentMetadata> existing = documentMetadataRepository
                .findByDocument_Id(document.getId());

        DocumentMetadata documentMetadata;
        if (existing.isPresent()) {
            documentMetadata = existing.get();
            documentMetadata.setMetadata(metadata);
        } else {
            documentMetadata = DocumentMetadata.builder()
                    .document(document)
                    .metadata(metadata)
                    .build();
        }

        documentMetadataRepository.save(documentMetadata);
    }

    @Transactional(readOnly = true)
    public Optional<DocumentMetadata> getMetadata(Long documentId) {
        return documentMetadataRepository.findByDocument_Id(documentId);
    }

    @Transactional
    public void extractAndSave(Long documentId, String originalFileName, String filePath) {
        try (InputStream inputStream = s3Service.getFile(filePath)) {

            dev.langchain4j.data.document.Document document = FileUtils.toDocumentWithMetadata(inputStream);
            dev.langchain4j.data.document.Metadata langchainMetadata = document.metadata();

            Map<String, Object> metadataMap = new HashMap<>();

            String author = langchainMetadata.getString("dc:creator");
            if (author == null || author.isBlank()) {
                author = langchainMetadata.getString("meta:author");
            }
            if (author != null && !author.isBlank()) {
                metadataMap.put("author", author);
            }

            String pageCount = langchainMetadata.getString("xmpTPg:NPages");
            if (pageCount == null || pageCount.isBlank()) {
                pageCount = langchainMetadata.getString("meta:page-count");
            }
            if (pageCount != null && !pageCount.isBlank()) {
                try {
                    metadataMap.put("page_count", Integer.parseInt(pageCount.trim()));
                } catch (NumberFormatException ignored) {
                }
            }

            String created = langchainMetadata.getString("dcterms:created");
            if (created != null && !created.isBlank()) {
                metadataMap.put("created_date", created);
            }

            String modified = langchainMetadata.getString("dcterms:modified");
            if (modified != null && !modified.isBlank()) {
                metadataMap.put("modified_date", modified);
            }

            metadataMap.put("file_name", originalFileName);

            saveMetadata(documentId, metadataMap);

        } catch (Exception e) {
            System.out.println("Extract metadata failed (khong chan ingest): " + e.getMessage());
        }
    }
}