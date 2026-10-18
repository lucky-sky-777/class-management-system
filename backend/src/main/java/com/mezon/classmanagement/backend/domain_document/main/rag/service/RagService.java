package com.mezon.classmanagement.backend.domain_document.main.rag.service;

import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.ChunkStrategy;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.MultipartFileDocumentSource;
import com.mezon.classmanagement.backend.domain_document.main.rag.dto.reponse.RagAnswerResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class RagService {

    GeminiRagService geminiRagService;
    ChunkService chunkService;
    ChunkStrategy chunkByParagraphWithoutOverlapStrategy;

    public RagAnswerResponseDto askInDocument(Long documentId, String question) {
        return RagAnswerResponseDto.builder()
                .question(question)
                .answer("")
                .build();
    }

    public RagAnswerResponseDto askQuestionOnDocument(MultipartFile file, String question) throws Exception {
        DocumentSource documentSource = new MultipartFileDocumentSource(file);

        List<String> chunkList = chunkService.getChunkList(
                chunkByParagraphWithoutOverlapStrategy,
                documentSource
        );

        List<String> contextChunks = chunkList.size() > 15 ? chunkList.subList(0, 15) : chunkList;

        String answer = geminiRagService.askGeminiRag(question, contextChunks);

        return RagAnswerResponseDto.builder()
                .question(question)
                .answer(answer)
                .build();
    }

}