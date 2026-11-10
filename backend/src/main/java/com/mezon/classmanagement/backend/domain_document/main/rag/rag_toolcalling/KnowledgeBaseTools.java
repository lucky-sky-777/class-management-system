package com.mezon.classmanagement.backend.domain_document.main.rag.rag_toolcalling;

import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.service.DocumentChunkService;
import com.mezon.classmanagement.backend.domain_document.main.search.service.SearchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.tool.annotation.Tool;
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

	@Tool(
			name = "searchKnowledgeBase",
			description = """
					Tìm kiếm thông tin trong kho tài liệu của hệ thống.
					
					Sử dụng công cụ này khi người dùng hỏi về:
					- Quy định của lớp.
					- Điểm danh.
					- Xin nghỉ học.
					- Điểm hoạt động.
					- Các thông tin có trong tài liệu.
					
					Hãy truyền nội dung cần tìm kiếm vào query.
					"""
	)
	public String searchKnowledgeBase(
			@ToolParam(description = "Nội dung cần tìm kiếm")
			String query
	) {
		List<DocumentChunk> documentChunks = searchService.searchAll(query, 5);

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