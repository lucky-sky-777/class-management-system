package com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/tags")
@RestController
public class TagController {

	@PostMapping
	public ResponseDTO<Void> create(String tag) {
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
		return ResponseDTO.ok(
				"Delete tag successful",
				null
		);
	}

}