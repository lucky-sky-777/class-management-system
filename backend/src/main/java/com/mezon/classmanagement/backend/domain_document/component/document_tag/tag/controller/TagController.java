package com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.dto.TagCreateRequest;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.dto.TagResponse;
import java.util.List;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.service.TagService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/tags")
@RestController
public class TagController {

	TagService  tagService;

	@GetMapping
	public ResponseDTO<List<TagResponse>> getAll() {
		List<TagResponse> data = tagService.getAllTags()
				.stream()
				.map(tag -> TagResponse.builder()
						.id(tag.getId())
						.name(tag.getName())
						.slug(tag.getSlug())
						.build())
				.toList();
		return ResponseDTO.ok(
				"Get all tags successfull",
				data
		);
	}

	@PostMapping
	public ResponseDTO<Void> create(
			@RequestBody TagCreateRequest request
	) {
		tagService.create(request.getName());
		return ResponseDTO.ok(
				"Create tag successful",
				null
		);
	}

	@DeleteMapping("/{tagId}")
	public ResponseDTO<Void> delete(
			@PathVariable
			Long tagId
	) {
		tagService.delete(tagId);
		return ResponseDTO.ok(
				"Delete tag successful",
				null
		);
	}

}