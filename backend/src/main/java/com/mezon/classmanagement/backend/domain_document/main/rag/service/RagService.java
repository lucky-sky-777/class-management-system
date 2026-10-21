package com.mezon.classmanagement.backend.domain_document.main.rag.service;

import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.repository.DocumentChunkRepository; // hoặc DocumentChunkService
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.repository.DocumentRepository;
import com.mezon.classmanagement.backend.domain_document.main.rag.dto.reponse.RagAnswerResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class RagService {

    GeminiRagService geminiRagService;
    DocumentRepository documentRepository;
    DocumentChunkRepository documentChunkRepository;

    public RagAnswerResponseDto askInDocument(Long documentId, String question) throws Exception {
        // 1. Kiểm tra tài liệu tồn tại
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài liệu ID: " + documentId));

        // 2. Lấy các chunk đã được IngestService lưu sẵn trong DB
        // (Kiểm tra đúng tên hàm trong DocumentChunkRepository, ví dụ: findByDocument_IdOrderByIndexAsc)
        List<DocumentChunk> chunks = documentChunkRepository.findByDocument_IdOrderByIndexAsc(documentId);

        if (chunks.isEmpty()) {
            return RagAnswerResponseDto.builder()
                    .question(question)
                    .answer("Tài liệu đang trong quá trình xử lý hoặc chưa được phân tích nội dung.")
                    .build();
        }

        // 3. Chuyển đổi thành List<String> nội dung văn bản
        List<String> contextList = chunks.stream()
                .map(DocumentChunk::getContent)
                .limit(20) // Giới hạn số lượng context chunks gửi cho Gemini
                .toList();

        // 4. Gọi Gemini trả lời
        String answer = geminiRagService.askGeminiRag(question, contextList);

        return RagAnswerResponseDto.builder()
                .question(question)
                .answer(answer)
                .build();
    }
}