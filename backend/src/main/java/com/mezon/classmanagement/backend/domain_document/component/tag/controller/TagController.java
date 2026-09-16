package com.mezon.classmanagement.backend.domain_document.component.tag.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain_document.component.tag.service.TagService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/documents")
@RestController
public class TagController {

	TagService tagService;


//	@GetMapping(
//			value = "/tags",
//			consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//	)
//	public ResponseDTO<String> getTagList() {
//		List<String> tagList = tagService.extractTagsWithVnCoreNLP()
//	}

}