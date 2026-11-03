package com.mezon.classmanagement.backend.domain_document.main.recommendation.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
@RestController
public class RecommendationController {

	@GetMapping("/users/{userId}")
	public ResponseDTO<List<DocumentResponseDto>> getDocumentRecommendationList(
			@PathVariable Long userId
	) {
		return ResponseDTO.ok("");
	}

	@GetMapping("/classes/{classId}")
	public ResponseDTO<List<DocumentResponseDto>> getClassRecommendationList(
			@PathVariable Long classId
	) {
		return ResponseDTO.ok("");
	}

}