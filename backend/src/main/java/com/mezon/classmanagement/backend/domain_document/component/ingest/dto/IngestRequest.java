package com.mezon.classmanagement.backend.domain_document.component.ingest.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record IngestRequest(
		Long creatorUserId,
		Long classId,
		Long directoryId,
		MultipartFile multipartFile
) {
}