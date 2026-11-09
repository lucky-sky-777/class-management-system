package com.mezon.classmanagement.backend.search;

import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.embedding.util.EmbeddingUtils;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.BaseVector;
import com.mezon.classmanagement.backend.domain_document.main.search.service.SearchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Scanner;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class SearchTest {

	EmbeddingService embeddingService;
	SearchService searchService;

	private void search(String query, float[] queryEmbedding) {
		List<DocumentChunk> documentChunkList = searchService.searchAll(queryEmbedding, 5);

		System.out.println("nội dung tìm kiếm: " + query);

		System.out.println("kết quả tìm kiếm:");
		for (int i = 0; i < documentChunkList.size(); i++) {
			System.out.println("===== " + (i + 1) + " =====\n");
			System.out.println(
					"Độ tương đồng: " + EmbeddingUtils.cosine(
							queryEmbedding,
							documentChunkList.get(i).getEmbedding().getValues()
					)
			);
			System.out.println(
					"Khoảng cách: " + EmbeddingUtils.cosineDistance(
							queryEmbedding,
							documentChunkList.get(i).getEmbedding().getValues()
					)
			);
			System.out.println(documentChunkList.get(i).getContent());
			System.out.println();
		}
	}

	@Test
	public void searchTest() {
		List<String> queryList = List.of(
				"mối quan hệ giữa cái chung, cái riêng",
				"vì sao trái đất hình cầu",
				"ngôn ngữ lập trình java"
		);

		queryList.forEach(item -> {
			search(item, embeddingService.embedSingleQueryText(item).getValues());
		});
	}

}