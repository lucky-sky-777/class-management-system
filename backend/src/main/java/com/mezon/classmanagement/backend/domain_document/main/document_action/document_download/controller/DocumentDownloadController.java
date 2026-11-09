package com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.dto.DocumentDownloadResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.service.DocumentDownloadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/document-downloads/")
@RestController
public class DocumentDownloadController {

	DocumentDownloadService documentDownloadService;

	AuthService authService;
	JwtService jwtService;

	@PostMapping("/{documentId}")
	public ResponseDTO<Void> create(
			@PathVariable Long documentId
	) {
		Authentication authentication = authService.getAuthentication();
		Long creatorUserId = jwtService.extractUserIdFromAuthentication(authentication);

		documentDownloadService.create(creatorUserId, documentId);

		return ResponseDTO.ok("Successful");
	}

	@GetMapping("/{documentId}")
	public ResponseDTO<List<DocumentDownloadResponseDto>> getListByDocument(
			@PathVariable Long documentId
	) {
		List<DocumentDownloadResponseDto> response = documentDownloadService.getListByDocument(documentId);

		return ResponseDTO.ok(
				"Get document download list by document successful",
				response
		);
	}

	@GetMapping("/users/current")
	public ResponseDTO<List<DocumentDownloadResponseDto>> getListByCurrentUser() {
		Authentication authentication = authService.getAuthentication();
		Long creatorUserId = jwtService.extractUserIdFromAuthentication(authentication);

		List<DocumentDownloadResponseDto> response = documentDownloadService.getListByUser(creatorUserId);

		return ResponseDTO.ok(
				"Get document download list by current user successful",
				response
		);
	}

}