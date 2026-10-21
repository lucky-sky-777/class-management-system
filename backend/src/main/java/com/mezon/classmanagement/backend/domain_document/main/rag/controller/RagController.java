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

    // @PostMapping("/ask")
    // public ResponseEntity<RagAnswerResponseDto> askQuestion(
    //         @RequestParam("file") MultipartFile file,
    //         @RequestParam("question") String question
    // ) {
    //     try {
    //         RagAnswerResponseDto response = ragService.askQuestionOnDocument(file, question);
    //         return ResponseEntity.ok(response);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.internalServerError().build();
    //     }
    // }

    @PostMapping("/ask")
    public ResponseEntity<RagAnswerResponseDto> askQuestion(
            @Valid @RequestBody RagAskRequestDto request
    ) {
        try {
            RagAnswerResponseDto response = ragService.askInDocument(
                    request.getDocumentId(),
                    request.getQuestion()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
