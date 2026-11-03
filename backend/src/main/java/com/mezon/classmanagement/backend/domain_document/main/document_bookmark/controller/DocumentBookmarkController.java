package com.mezon.classmanagement.backend.domain_document.main.document_bookmark.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain_document.main.document_bookmark.dto.DocumentBookmarkResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document_bookmark.service.DocumentBookmarkService;
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
@RequestMapping("/api/document-bookmarks/")
@RestController
public class DocumentBookmarkController {

	DocumentBookmarkService documentBookmarkService;

	AuthService authService;
	JwtService jwtService;

	@PostMapping("/{documentId}")
	public ResponseDTO<Void> saveOrUnsave(
			@PathVariable Long documentId
	) {
		Authentication authentication = authService.getAuthentication();
		Long creatorUserId = jwtService.extractUserIdFromAuthentication(authentication);

		documentBookmarkService.saveOrUnsave(creatorUserId, documentId);

		return ResponseDTO.ok("Successful");
	}

	@GetMapping("/users/current")
	public ResponseDTO<List<DocumentBookmarkResponseDto>> getListByCurrentUser() {
		Authentication authentication = authService.getAuthentication();
		Long creatorUserId = jwtService.extractUserIdFromAuthentication(authentication);

		List<DocumentBookmarkResponseDto> response = documentBookmarkService.getListByUser(creatorUserId);

		return ResponseDTO.ok(
				"Get document bookmark list by current user successful",
				response
		);
	}

	@GetMapping("/{documentId}")
	public ResponseDTO<List<DocumentBookmarkResponseDto>> getListByDocument(
			@PathVariable Long documentId
	) {
		List<DocumentBookmarkResponseDto> response = documentBookmarkService.getListByDocument(documentId);

		return ResponseDTO.ok(
				"Get document bookmark list by document successful",
				response
		);
	}

}