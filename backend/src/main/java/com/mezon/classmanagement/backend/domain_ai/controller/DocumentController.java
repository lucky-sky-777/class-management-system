package com.mezon.classmanagement.backend.domain_ai.controller;


import com.mezon.classmanagement.backend.domain_ai.service.DocumentIngestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/documents")
public class DocumentController {

	private final DocumentIngestService ingestService;

	@PostMapping(
			value = "/upload",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public void upload(
			@RequestParam Long classId,
			@RequestParam MultipartFile file
	) {

		//ingestService.ingest(file);
	}

}