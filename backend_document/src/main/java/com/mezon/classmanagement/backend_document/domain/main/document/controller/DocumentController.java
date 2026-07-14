package com.mezon.classmanagement.backend_document.domain.main.document.controller;

import com.mezon.classmanagement.backend_document.domain.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend_document.common.util.GeminiTokenCountEstimator;
import com.mezon.classmanagement.backend_document.domain.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend_document.domain.component.ingest.service.DocumentIngestService;
import com.mezon.classmanagement.backend_document.domain.component.split.strategy.impl.SplitByParagraphStrategy;
import com.mezon.classmanagement.backend_document.domain.component.vector.entity.impl.Vector3072;
import com.mezon.classmanagement.backend_document.domain.main.document.dto.TextRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

	DocumentIngestService ingestService;
	EmbeddingService embeddingService;
	ChunkService chunkService;

	SplitByParagraphStrategy splitByParagraphStrategy;

	@PostMapping(
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public void upload(
			@RequestParam MultipartFile file
	) {
		//ingestService.ingest(file);
	}

	@PostMapping(
			value = "/chunk",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<Void> chunk(
			//@RequestParam MultipartFile file
	) {
		System.out.println("start chunk");
//		List<String> chunkList = chunkService.getChunkListFromMultipartFile(
//				file,
//				splitByParagraphStrategy
//		);
//
//		chunkList.forEach(System.out::println);
		System.out.println("end chunk");
		return ResponseEntity
				.accepted()
				.build();
	}

	@PostMapping("/embed")
	public void embed(
			@RequestParam String text
	) throws Exception {
		Vector3072 vector3072 = embeddingService.embedSingleQueryText(text);

		System.out.println(embeddingService.toVectorString(vector3072));
	}

	@PostMapping("/token")
	public void token(
			@RequestBody
			TextRequest request
	) {
		System.out.println(request.text());
		System.out.println(new GeminiTokenCountEstimator().estimateTokenCountInText(request.text()));
	}

}