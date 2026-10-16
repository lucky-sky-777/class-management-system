package com.mezon.classmanagement.backend.domain_document.main.rag.service;

import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.ChunkStrategy;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.MultipartFileDocumentSource;
import com.mezon.classmanagement.backend.domain_document.main.rag.dto.reponse.RagAnswerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RagService {

    private final GeminiRagService geminiRagService;
    private final ChunkService chunkService;
    private final ChunkStrategy chunkByParagraphWithoutOverlapStrategy;

    public RagAnswerResponseDto askQuestionOnDocument(MultipartFile file, String question) throws Exception {
        // 1. Chuyển đổi file tải lên thành DocumentSource
        DocumentSource documentSource = new MultipartFileDocumentSource(file);

        // 2. Cắt văn bản thành các chunks
        List<String> chunkList = chunkService.getChunkList(
                chunkByParagraphWithoutOverlapStrategy,
                documentSource
        );

        // TODO: Tương lai sẽ thay bằng query tìm kiếm Vector Cosine Similarity
        List<String> contextChunks = chunkList.size() > 15 ? chunkList.subList(0, 15) : chunkList;

        // 3. Gọi Gemini sinh câu trả lời
        String answer = geminiRagService.askGeminiRag(question, contextChunks);

        // 4. Đóng gói trả về DTO
        return RagAnswerResponseDto.builder()
                .question(question)
                .answer(answer)
                .build();
    }
}
