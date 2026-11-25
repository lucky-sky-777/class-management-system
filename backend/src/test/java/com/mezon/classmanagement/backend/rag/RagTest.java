package com.mezon.classmanagement.backend.rag;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.config.SplitConfig;
import com.mezon.classmanagement.backend.domain_document.component.chunk.formatter.ChunkFormatterPipeline;
import com.mezon.classmanagement.backend.domain_document.component.chunk.formatter.impl.GeminiChunkFormatter;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.repository.DocumentChunkRepository;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.service.DocumentChunkService;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.MultipartFileDocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.embedding.util.EmbeddingUtils;
import com.mezon.classmanagement.backend.domain_document.component.parser.impl.TikaV4XDocumentParser;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.custom.VnCoreNLPDocumentBySentenceSplitter;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.BaseVector;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.rag.dto.reponse.RagAnswerResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.rag.service.RagService;
import com.mezon.classmanagement.backend.domain_document.main.search.service.SearchService;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings(value = {WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class RagTest {

	EmbeddingService embeddingService;
	RagService ragService;
	SearchService searchService;
	DocumentChunkService documentChunkService;
	DocumentChunkRepository documentChunkRepository;

	@Test
	public void demoChunking() throws Exception {
		String fileName = "triethocmaclenin.docx";
		ClassPathResource classPathResource = new ClassPathResource(fileName);

		MockMultipartFile multipartFile = new MockMultipartFile(
				fileName,
				fileName,
				FileUtils.detectMimeTypeFromInputStream(
						classPathResource.getInputStream(),
						fileName
				),
				classPathResource.getInputStream()
		);

		DocumentSplitter documentSplitter = new DocumentByParagraphSplitter(
				SplitConfig.MAX_SEGMENT_SIZE,
				SplitConfig.MAX_OVERLAP_SIZE,
				SplitConfig.OPEN_AI_TOKEN_COUNT_ESTIMATOR
		);

		Metadata metadata = new Metadata();
		String originalFileName = multipartFile.getName();

		if (!originalFileName.isBlank()) {
			metadata.set(
					TikaCoreProperties.RESOURCE_NAME_KEY,
					originalFileName
			);
		}

		dev.langchain4j.data.document.Document document = new TikaV4XDocumentParser(
				metadata
		).parse(
				multipartFile.getInputStream()
		);

		List<TextSegment> textSegmentList = documentSplitter
				.split(document);

		List<String> chunkList = new ArrayList<>(textSegmentList.size());
		for (TextSegment textSegment : textSegmentList) {
			chunkList.add(
					textSegment.text().trim().replaceAll("\\s+", " ")
			);
		}

		// Lưu vào database
		/*
		List<BaseVector> embeddingList = embeddingService
				.embedMultipleDocumentText(
						multipartFile.getOriginalFilename(),
						new ChunkFormatterPipeline(
								List.of(
										new GeminiChunkFormatter()
								)
						).formatList(
								chunkList,
								new dev.langchain4j.data.document.Metadata()
										.put(
												"fileName",
												multipartFile.getOriginalFilename()
										)
						)
				);

		List<DocumentChunk> documentChunkList = new ArrayList<>(textSegmentList.size());
		for (short i = 0; i < chunkList.size(); i++) {
			documentChunkList.add(
					DocumentChunk
							.builder()
							.document(Document.create(/* document_id *))
							.index(i)
							.content(chunkList.get(i))
							.embedding(
									new Vector1536(
											embeddingList.get(i).getValues()
									)
							)
							.build()
			);
		}
		documentChunkService.insertBatch(documentChunkList);
		*/

		for (int i = 0; i < chunkList.size(); i++) {
			System.out.println("Chunk " + (i + 1));
			System.out.println(chunkList.get(i) + "\n");
		}
	}

	@Test
	public void demoSearch() throws Exception {
		String query = "cái chung và cái riêng";

		float[] queryEmbedding = embeddingService
				.embedSingleQueryText(query)
				.getValues();

		/*
		 * @Query(value = """
		 * SELECT *
		 * FROM document_chunks dc
		 * ORDER BY dc.embedding <=> CAST(:embedding AS vector) ASC
		 * LIMIT :limit
		 * """, nativeQuery = true
		 * )
		 * List<DocumentChunk> searchInAllSystemDocument(
		 *     float[] embedding,
		 *     int limit
		 * );
		 */
		List<DocumentChunk> documentChunkList = documentChunkRepository
				.searchInOneDocument(
						89L,
						queryEmbedding,
						2
				);

		System.out.println("nội dung tìm kiếm: " + query);
		System.out.println("kết quả tìm kiếm:\n");
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

		RagAnswerResponseDto response = ragService.askGlobal(query, true);

		System.out.println(response.getAnswer());
	}

}