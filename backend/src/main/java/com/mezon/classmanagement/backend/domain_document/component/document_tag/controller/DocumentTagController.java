package com.mezon.classmanagement.backend.domain_document.component.document_tag.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.dto.TagResponse;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.service.TagService;
import com.mezon.classmanagement.backend.domain_document.component.validator.ValidFile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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
	public ResponseDTO<List<TagResponse>> getTagList(
			@PathVariable Long documentId
	) {
		List<TagResponse> data = tagService.getTagsOfDocument(documentId)
				.stream()
				.map(tag -> TagResponse.builder()
						.id(tag.getId())
						.name(tag.getName())
						.slug(tag.getSlug())
						.build())
				.toList();

		return ResponseDTO.ok(
				"Get tag list successful",
				data
		);
	}

	@PatchMapping("/{documentId}/tags")
	public ResponseDTO<Void> updateTagList(
			@PathVariable Long documentId,
			@RequestBody Long[] tagIds
	) {
		tagService.replaceTags(documentId, tagIds);
		return ResponseDTO.ok(
				"Update tag list successful",
				null
		);
	}

}