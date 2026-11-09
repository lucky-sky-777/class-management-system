package com.mezon.classmanagement.backend.domain_document.main.rag.service;

import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.rag.dto.reponse.DocumentSourceDto;
import com.mezon.classmanagement.backend.domain_document.main.rag.dto.reponse.RagAnswerResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.search.service.SearchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RagService {

    GeminiRagService geminiRagService;
    SearchService searchService;
    int limit = 3;

    public RagAnswerResponseDto askInDocument(Long documentId, String question) throws Exception {
        List<DocumentChunk> chunks = searchService.searchDocument(documentId, question, limit);
        return processChunksAndCallGemini(chunks, question);
    }

    public RagAnswerResponseDto askInClass(Long classId, String question) throws Exception {
        List<DocumentChunk> chunks = searchService.searchClass(classId, question, limit);
        return processChunksAndCallGemini(chunks, question);
    }

    public RagAnswerResponseDto askGlobal(String question) throws Exception {
        List<DocumentChunk> chunks = searchService.searchAll(question, limit);
        return processChunksAndCallGemini(chunks, question);
    }

    private RagAnswerResponseDto processChunksAndCallGemini(List<DocumentChunk> chunks, String question) throws Exception {
        if (chunks == null || chunks.isEmpty()) {
            return RagAnswerResponseDto.builder()
                    .question(question)
                    .answer("Tài liệu hiện tại không đề cập đến thông tin này hoặc đang trong quá trình xử lý.")
                    .sources(List.of())
                    .build();
        }

        // 1. Trích xuất danh sách nguồn tài liệu độc lập (Lọc trùng an toàn theo ID của Document)
        List<DocumentSourceDto> sources = chunks.stream()
                .map(DocumentChunk::getDocument)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Document::getId,
                        doc -> DocumentSourceDto.builder()
                                .id(doc.getId())
                                .fileName(doc.getFileName())
                                .build(),
                        (existing, replacement) -> existing, // Giữ lại bản ghi đầu tiên nếu trùng ID
                        LinkedHashMap::new                   // Giữ nguyên thứ tự ưu tiên độ liên quan
                ))
                .values()
                .stream()
                .toList();

        // 2. Truyền thẳng List<DocumentChunk> vào GeminiRagService
        String answer = geminiRagService.askGeminiRag(question, chunks);

        // 3. Trả về kết quả hoàn chỉnh kèm sources cho Frontend
        return RagAnswerResponseDto.builder()
                .question(question)
                .answer(answer)
                .sources(sources)
                .build();
    }
}