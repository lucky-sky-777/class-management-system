package com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
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

	@PostMapping
	public ResponseDTO<Void> create(
			@RequestBody String tag
	) {
		tagService.create(tag);
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