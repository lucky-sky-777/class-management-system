package com.mezon.classmanagement.backend.domain_document.main.rag.controller;

import com.mezon.classmanagement.backend.domain_document.main.rag.dto.reponse.RagAnswerResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.rag.dto.request.RagAskRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.rag.service.RagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RagController {

    private final RagService ragService;

    /**
     * 1. Hỏi trên toàn hệ thống (Global)
     * URL: POST /api/rag/ask
     */
    @PostMapping()
    public ResponseEntity<RagAnswerResponseDto> askGlobal(
            @Valid @RequestBody RagAskRequestDto request
    ) {
        try {
            RagAnswerResponseDto response = ragService.askGlobal(request.getQuestion());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 2. Hỏi trong phạm vi 1 lớp học (Class-level)
     * URL: POST /api/rag/class/{classId}/ask
     */
    @PostMapping("/class/{classId}")
    public ResponseEntity<RagAnswerResponseDto> askInClass(
            @PathVariable Long classId,
            @Valid @RequestBody RagAskRequestDto request
    ) {
        try {
            RagAnswerResponseDto response = ragService.askInClass(classId, request.getQuestion());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 3. Hỏi trên 1 tài liệu cụ thể (Document-level)
     * URL: POST /api/rag/class/{classId}/document/{documentId}/ask
     */
    @PostMapping("/class/{classId}/document/{documentId}")
    public ResponseEntity<RagAnswerResponseDto> askInDocument(
            @PathVariable Long classId, // Có thể dùng hoặc bỏ qua nếu documentId đã đủ định danh
            @PathVariable Long documentId,
            @Valid @RequestBody RagAskRequestDto request
    ) {
        try {
            // Giữ lại hàm cũ của bạn
            RagAnswerResponseDto response = ragService.askInDocument(documentId, request.getQuestion());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}