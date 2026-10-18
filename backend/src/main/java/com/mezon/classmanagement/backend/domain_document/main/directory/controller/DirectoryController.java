package com.mezon.classmanagement.backend.domain_document.main.directory.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.security.annotation.Public;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain_document.main.directory.dto.CreateDirectoryRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.directory.dto.DirectoryResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.directory.service.DirectoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/directories")
@RestController
public class DirectoryController {

	AuthService authService;
	JwtService jwtService;

	DirectoryService directoryService;

	@Public
	@PostMapping("/classes/{classId}")
	public ResponseDTO<DirectoryResponseDto> create(
			@PathVariable Long classId,
			@RequestBody CreateDirectoryRequestDto request
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		DirectoryResponseDto response = directoryService.create(classId, userId, request);

		return ResponseDTO.ok(
				"Create directory successful",
				response
		);
	}

	@PatchMapping("/{directoryId}")
	public void update(
			@PathVariable Long directoryId
	) {
		//
	}

	@DeleteMapping("/{directoryId}")
	public void delete(
			@PathVariable Long directoryId
	) throws Exception {
		//
	}

	@GetMapping("/classes/{classId}")
	public ResponseDTO<List<DirectoryResponseDto>> getList(
			@PathVariable Long classId
	) {
		List<DirectoryResponseDto> response = directoryService.getList(classId);

		return ResponseDTO.ok(
				"Get directory list successful",
				response
		);
	}

}