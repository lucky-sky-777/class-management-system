package com.mezon.classmanagement.backend.domain_document.main.recommendation.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.service.RecommendationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.springframework.security.core.Authentication;
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

	RecommendationService recommendationService;

	AuthService authService;
	JwtService jwtService;

	@GetMapping
	public ResponseDTO<List<DocumentResponseDto>> getDocumentRecommendationList() {
		Authentication authentication = authService.getAuthentication();
		Long clientUserId = jwtService.extractUserIdFromAuthentication(authentication);

		List<DocumentResponseDto> response = recommendationService.getCandidate(clientUserId);

		return ResponseDTO.ok(
				"Get recommendation list successful",
				response
		);
	}

	@GetMapping("/classes/{classId}")
	public ResponseDTO<List<DocumentResponseDto>> getClassRecommendationList(
			@PathVariable Long classId
	) {
		return ResponseDTO.ok("");
	}

}