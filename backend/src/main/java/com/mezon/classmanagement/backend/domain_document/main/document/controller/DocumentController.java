package com.mezon.classmanagement.backend.domain_document.main.document.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain_document.component.ingest.service.IngestService;
import com.mezon.classmanagement.backend.domain_document.component.validator.ValidFile;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.request.CreateDocumentRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import dev.langchain4j.data.document.Metadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/classes/{classId}/directories/{directoryId}/documents")
@RestController
public class DocumentController {

	AuthService authService;
	JwtService jwtService;

	DocumentService documentService;
	IngestService ingestService;

	@PostMapping(
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseDTO<DocumentResponseDto> upload(
			@PathVariable
			Long classId,

			@PathVariable
			Long directoryId,

			@RequestPart("request")
			CreateDocumentRequestDto request,

			@ValidFile
			@RequestPart("file")
			MultipartFile multipartFile
	) {
		Path tempFile;
		try {
			tempFile = Files.createTempFile("document-", ".tmp");
			multipartFile.transferTo(tempFile);
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR,
					e.getMessage()
			);
		}

		Authentication authentication = authService.getAuthentication();
		Long creatorUserId = jwtService.extractUserIdFromAuthentication(authentication);

		DocumentResponseDto response = documentService.create(
				creatorUserId, directoryId, request, multipartFile
		);

		try {
			ingestService.ingest(
					classId,
					directoryId,
					response.getId(),
					tempFile,
					new Metadata()
							.put("fileName", multipartFile.getOriginalFilename())
							.put("fileExtension", FileUtils.getExtension(multipartFile))
							.put("fileMimeType", FileUtils.detectMimeTypeFromMultipartFile(multipartFile))
							.put("fileSize", multipartFile.getSize()),
					response.getKey()
			);
		} catch (Exception e) {}
		response.setKey(null);

		return ResponseDTO.ok("uploading", response);
	}

}