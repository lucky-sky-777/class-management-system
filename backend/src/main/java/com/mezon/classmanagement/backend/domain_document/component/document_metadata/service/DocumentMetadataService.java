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
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain_document.component.s3.service.S3Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DocumentMetadataService {
    DocumentMetadataRepository  documentMetadataRepository;
    DocumentRepository  documentRepository;

    S3Service s3Service;

    private static final Logger log = LoggerFactory.getLogger(DocumentMetadataService.class);

    private static final Set<String> EDITABLE_FIELDS = Set.of(
            "author"
    );

    private static final int MAX_TEXT_LENGTH = 255;

    @Transactional
    public void saveMetadata(Long documentId, Map<String, Object> metadata){
        Document document = documentRepository
                .findById(documentId)
                .orElse(null);

        if(document == null){
            log.warn("Skip metadata. Document not found: {}", documentId);
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
    public void patchMetadata(Long documentId, Map<String, Object> patch) {
        if (patch == null || patch.isEmpty()) {
            throw new GlobalException(
                    GlobalException.Type.INVALID_REQUEST,
                    "Metadata patch is empty"
            );
        }

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new GlobalException(
                        GlobalException.Type.NOT_EXISTS,
                        "Document not found"
                ));

        Set<String> rejected = new TreeSet<>();
        for (String key : patch.keySet()) {
            if (!EDITABLE_FIELDS.contains(key)) {
                rejected.add(key);
            }
        }
        if (!rejected.isEmpty()) {
            throw new GlobalException(
                    GlobalException.Type.INVALID_REQUEST,
                    "Fields are not editable: " + rejected
                            + ". Allowed fields: " + EDITABLE_FIELDS
            );
        }

        for (Map.Entry<String, Object> entry : patch.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (!(value instanceof String text)) {
                throw new GlobalException(
                        GlobalException.Type.INVALID_REQUEST,
                        "Field '" + key + "' must be a string"
                );
            }
            if (text.isBlank()) {
                throw new GlobalException(
                        GlobalException.Type.INVALID_REQUEST,
                        "Field '" + key + "' must not be blank"
                );
            }
            if (text.length() > MAX_TEXT_LENGTH) {
                throw new GlobalException(
                        GlobalException.Type.INVALID_REQUEST,
                        "Field '" + key + "' exceeds " + MAX_TEXT_LENGTH + " characters"
                );
            }
        }

        Map<String, Object> merged = new HashMap<>();

        documentMetadataRepository.findByDocument_Id(documentId)
                .ifPresent(existing -> merged.putAll(existing.getMetadata()));

        merged.putAll(patch);

        saveMetadata(document.getId(), merged);
    }

    @Transactional
    public void deleteMetadata(Long documentId) {
        DocumentMetadata documentMetadata = documentMetadataRepository
                .findByDocument_Id(documentId)
                .orElseThrow(() -> new GlobalException(
                        GlobalException.Type.NOT_EXISTS,
                        "Metadata not found"
                ));

        documentMetadataRepository.delete(documentMetadata);
    }

    public Map<String, Object> extract(MultipartFile multipartFile) {
        Map<String, Object> metadataMap = new HashMap<>();

        metadataMap.put("file_name", multipartFile.getOriginalFilename());
        metadataMap.put("file_size", multipartFile.getSize());

        try {
            metadataMap.put("mime_type",
                    FileUtils.detectMimeTypeFromMultipartFile(multipartFile));
        } catch (Exception e) {
            log.warn("Detect mime type failed, fallback to content-type header", e);
            metadataMap.put("mime_type", multipartFile.getContentType());
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            dev.langchain4j.data.document.Document document =
                    FileUtils.toDocumentWithMetadata(inputStream);
            dev.langchain4j.data.document.Metadata langchainMetadata = document.metadata();

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

        } catch (Exception e) {
            log.warn("Tika extract metadata failed", e);
        }

        return metadataMap;
    }
}