package com.mezon.classmanagement.backend.domain_document.main.document.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.util.GeminiTokenCountEstimator;
import com.mezon.classmanagement.backend.common.validator.FileValidator;
import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.ingest.service.IngestService;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.SplitByParagraphStrategy;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector3072;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.TextRequest;
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

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

	IngestService ingestService;
	EmbeddingService embeddingService;
	ChunkService chunkService;
	GeminiTokenCountEstimator geminiTokenCountEstimator;

	SplitByParagraphStrategy splitByParagraphStrategy;

	@PostMapping(
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public void upload(
			@RequestParam MultipartFile file
	) {
		FileValidator.validateFileType(file);
		ingestService.ingest(file);
	}

	@PostMapping(
			value = "/chunk",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<ResponseDTO<String>> chunk(
			@RequestParam MultipartFile file
	) throws Exception {
		FileValidator.validateFileType(file);
		System.out.println("start chunk");

		ingestService.ingest(file);

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
		System.out.println(geminiTokenCountEstimator.estimateTokenCountInText(request.text()));
	}

}