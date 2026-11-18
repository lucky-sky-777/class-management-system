package com.mezon.classmanagement.backend.domain_document.component.document_metadata.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain_document.component.document_metadata.dto.DocumentMetadataResponseDto;
import com.mezon.classmanagement.backend.domain_document.component.document_metadata.entity.DocumentMetadata;
import com.mezon.classmanagement.backend.domain_document.component.document_metadata.service.DocumentMetadataService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PatchMapping("/{documentId}/metadata")
    public ResponseDTO<Void> patchMetadata(
            @PathVariable
            Long documentId,

            @RequestBody
            Map<String, Object> metadata
    ) {
        documentMetadataService.patchMetadata(documentId, metadata);

        return ResponseDTO.ok(
                "Update metadata successful",
                null
        );
    }




}