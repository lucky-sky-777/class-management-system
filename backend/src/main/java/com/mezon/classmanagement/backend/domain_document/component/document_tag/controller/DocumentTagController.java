package com.mezon.classmanagement.backend.domain_document.component.document_tag.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.entity.Tag;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.service.TagService;
import com.mezon.classmanagement.backend.domain_document.component.validator.ValidFile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/document-tags")
@RestController
public class DocumentTagController {

	TagService tagService;

	@GetMapping(
			path = "/extract",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseDTO<List<String>> extractTagList(
			@RequestParam
			@ValidFile
			MultipartFile multipartFile
	) throws Exception {
		List<String> tagList = tagService.extractTagsWithVnCoreNLP(multipartFile, 20);

		return ResponseDTO.ok(
				"Extract tag list successful",
				tagList
		);
	}

	@GetMapping("/{documentId}/tags")
	public ResponseDTO<List<Tag>> getTagList(
			@PathVariable Long documentId
	) {
		return ResponseDTO.ok(
				"Get tag list successful",
				null
		);
	}

	@PatchMapping("/{documentId}/tags")
	public ResponseDTO<Void> updateTagList(
			@PathVariable Long documentId,
			Long[] tagIds
	) {
		return ResponseDTO.ok(
				"Update tag list successful",
				null
		);
	}

}