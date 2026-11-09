package com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.dto.DocumentViewResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.service.DocumentViewService;
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
@RequestMapping("/api/document-views/")
@RestController
public class DocumentViewController {

	DocumentViewService documentViewService;

	AuthService authService;
	JwtService jwtService;

	@PostMapping("/{documentId}")
	public ResponseDTO<Void> saveOrUnsave(
			@PathVariable Long documentId
	) {
		Authentication authentication = authService.getAuthentication();
		Long creatorUserId = jwtService.extractUserIdFromAuthentication(authentication);

		documentViewService.create(creatorUserId, documentId);

		return ResponseDTO.ok("Successful");
	}

	@GetMapping("/{documentId}")
	public ResponseDTO<List<DocumentViewResponseDto>> getListByDocument(
			@PathVariable Long documentId
	) {
		List<DocumentViewResponseDto> response = documentViewService.getListByDocument(documentId);

		return ResponseDTO.ok(
				"Get document view list by document successful",
				response
		);
	}

	@GetMapping("/users/current")
	public ResponseDTO<List<DocumentViewResponseDto>> getListByCurrentUser() {
		Authentication authentication = authService.getAuthentication();
		Long creatorUserId = jwtService.extractUserIdFromAuthentication(authentication);

		List<DocumentViewResponseDto> response = documentViewService.getListByUser(creatorUserId);

		return ResponseDTO.ok(
				"Get document view list by current user successful",
				response
		);
	}

}