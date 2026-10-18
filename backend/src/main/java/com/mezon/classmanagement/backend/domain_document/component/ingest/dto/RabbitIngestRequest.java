package com.mezon.classmanagement.backend.domain_document.component.ingest.dto;

public record RabbitIngestRequest(
		Long classId,
		Long documentId,
		String tempFilePath,
		String originalFileName
) {
}