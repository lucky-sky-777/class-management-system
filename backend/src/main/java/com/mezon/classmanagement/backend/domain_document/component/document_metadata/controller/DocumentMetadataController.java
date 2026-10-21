package com.mezon.classmanagement.backend.domain_document.component.document_metadata.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain_document.component.document_metadata.dto.DocumentMetadataResponseDto;
import com.mezon.classmanagement.backend.domain_document.component.document_metadata.entity.DocumentMetadata;
import com.mezon.classmanagement.backend.domain_document.component.document_metadata.service.DocumentMetadataService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/documents")
@RestController
public class DocumentMetadataController {

    DocumentMetadataService documentMetadataService;

    @GetMapping("/{documentId}/metadata")
    public ResponseDTO<DocumentMetadataResponseDto> getMetadata(
            @PathVariable
            Long documentId
    ) {
        DocumentMetadata documentMetadata = documentMetadataService
                .getMetadata(documentId)
                .orElse(null);

        if (documentMetadata == null) {
            return ResponseDTO.ok(
                    "No metadata found",
                    null
            );
        }

        DocumentMetadataResponseDto data = DocumentMetadataResponseDto.builder()
                .documentId(documentId)
                .metadata(documentMetadata.getMetadata())
                .build();

        return ResponseDTO.ok(
                "Get metadata successful",
                data
        );
    }

}