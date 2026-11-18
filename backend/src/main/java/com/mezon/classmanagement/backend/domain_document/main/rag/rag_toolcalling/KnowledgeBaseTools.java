package com.mezon.classmanagement.backend.domain_document.main.rag.rag_toolcalling;

import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.service.DocumentChunkService;
import com.mezon.classmanagement.backend.domain_document.main.search.service.SearchService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class KnowledgeBaseTools {

	SearchService searchService;

	@Tool("""
        Tìm kiếm thông tin trong kho tài liệu của hệ thống.

        Sử dụng công cụ này khi người dùng hỏi về:
        - Quy định của lớp.
        - Điểm danh.
        - Xin nghỉ học.
        - Điểm hoạt động.

        Truyền nội dung cần tìm kiếm vào query.
        """)
	public String searchKnowledgeBase(
			@P("Nội dung cần tìm kiếm") String query
	) {
		List<DocumentChunk> documentChunks = searchService.searchAll(query, 2);

		if (documentChunks.isEmpty()) {
			return "Không tìm thấy tài liệu phù hợp.";
		}

		return IntStream.range(0, documentChunks.size())
				.mapToObj(index -> {

					DocumentChunk document = documentChunks.get(index);

					return """
							Tài liệu %d
							Nguồn: %s
							Nội dung:
							%s
							""".formatted(
							index + 1,
							document.getDocument().getFileName(),
							document.getContent()
					);

				})
				.collect(Collectors.joining("\n----------------\n"));
	}

}