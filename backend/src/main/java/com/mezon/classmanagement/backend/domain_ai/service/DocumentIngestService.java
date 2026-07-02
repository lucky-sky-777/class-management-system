package com.mezon.classmanagement.backend.domain_ai.service;


import com.mezon.classmanagement.backend.domain_ai.entity.Document;
import com.mezon.classmanagement.backend.domain_ai.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_ai.entity.Vector3072;
import com.mezon.classmanagement.backend.domain_ai.repository.DocumentChunkRepository;
import com.mezon.classmanagement.backend.domain_ai.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentIngestService {

	//private final PdfService pdfService;

	private final TextChunkService textChunkService;

	private final EmbeddingService embeddingService;

	private final DocumentRepository documentRepository;

	private final DocumentChunkRepository chunkRepository;

//	public void ingest(MultipartFile file) {
//		Vector3072 vector3072 = embeddingService.embedMedia(file);
//		System.out.println(embeddingService.toVectorString(vector3072));
//		DocumentChunk entity =
//				DocumentChunk.builder()
//						.documentId(20L)
//						.chunkIndex(0)
//						.content(file.getName())
//						.embedding(vector3072)
//						.build();
//
//		chunkRepository.save(entity);
//	}

	public void ingest(
			Long classId,
			MultipartFile file
	) {

		String text = "java spring boot\nc sharp dot net";

		Document document =
				documentRepository.save(
						Document.builder()
								.classId(classId)
								.title(file.getOriginalFilename())
								.createdAt(Instant.now())
								.build()
				);

		List<String> chunks =
				textChunkService.chunk(text);

		for (int i = 0; i < chunks.size(); i++) {

			String chunk = chunks.get(i);

			Vector3072 embedding = null;//embeddingService.embed(chunk);

			DocumentChunk entity =
					DocumentChunk.builder()
							.documentId(document.getId())
							.chunkIndex(i)
							.content(chunk)
							.embedding(embedding)
							.build();

			//chunkRepository.save(entity);
		}
	}
}