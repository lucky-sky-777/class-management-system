package com.mezon.classmanagement.backend.domain_document.main.rag.controller;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.main.rag.dto.reponse.RagAnswerResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.rag.dto.request.RagAskRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.rag.service.RagService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings(value = {WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/rag")
@RestController
public class RagController {

	RagService ragService;

	/**
	 * 1. Hỏi trên toàn hệ thống (Global)
	 * URL: POST /api/rag/global
	 */
	@PostMapping("/global")
	public ResponseEntity<RagAnswerResponseDto> askGlobal(
			@Valid @RequestBody RagAskRequestDto request
	) {
		try {
			RagAnswerResponseDto response = ragService.askGlobal(request.getQuestion(), request.isUseHyde());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 2. Hỏi trong phạm vi 1 lớp học (Class-level)
	 * URL: POST /api/rag/classes/{classId}
	 */
	@PostMapping("/classes/{classId}")
	public ResponseEntity<RagAnswerResponseDto> askInClass(
			@PathVariable Long classId,
			@Valid @RequestBody RagAskRequestDto request
	) {
		try {
			RagAnswerResponseDto response = ragService.askInClass(classId, request.getQuestion(), request.isUseHyde());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 3. Hỏi trong 1 tài liệu cụ thể (Document-level)
	 * URL: POST /api/rag/classes/{classId}/documents/{documentId}
	 */
	@PostMapping("/classes/{classId}/documents/{documentId}")
	public ResponseEntity<RagAnswerResponseDto> askInDocument(
			@PathVariable Long classId,
			@PathVariable Long documentId,
			@Valid @RequestBody RagAskRequestDto request
	) {
		try {
			RagAnswerResponseDto response = ragService.askInDocument(documentId, request.getQuestion(), request.isUseHyde());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

}