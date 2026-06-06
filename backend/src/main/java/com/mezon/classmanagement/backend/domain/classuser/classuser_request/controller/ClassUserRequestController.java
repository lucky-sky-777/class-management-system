package com.mezon.classmanagement.backend.domain.classuser.classuser_request.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.security.annotation.Public;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.ClassUserRequestIdResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.response.ClassUserRequestResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.service.ClassUserRequestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/join-class-requests")
@RestController
public class ClassUserRequestController {

	AuthService authService;
	JwtService jwtService;

	ClassUserRequestService classUserRequestService;

	@PreAuthorize("@ClassSecurity.adminOnly(#classId)")
	@PatchMapping("/classes/{classId}/requests/{requestId}/approve")
	public ResponseDTO<ClassUserRequestIdResponseDto> approve(
			@PathVariable Long classId,
			@PathVariable Long requestId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		ClassUserRequestIdResponseDto response = classUserRequestService.approve(classId, userId, requestId);

		return ResponseDTO.<ClassUserRequestIdResponseDto>builder()
				.success(true)
				.message("Approve join class request successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassSecurity.adminOnly(#classId)")
	@PatchMapping("/classes/{classId}/requests/{requestId}/reject")
	public ResponseDTO<ClassUserRequestIdResponseDto> reject(
			@PathVariable Long classId,
			@PathVariable Long requestId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		ClassUserRequestIdResponseDto response = classUserRequestService.reject(classId, userId, requestId);

		return ResponseDTO.<ClassUserRequestIdResponseDto>builder()
				.success(true)
				.message("Reject join class request successful")
				.data(response)
				.build();
	}

	@Public
	@PatchMapping("/classes/{classId}/requests/{requestId}/cancel")
	public ResponseDTO<ClassUserRequestIdResponseDto> cancel(
			@PathVariable Long classId,
			@PathVariable Long requestId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		ClassUserRequestIdResponseDto response = classUserRequestService.cancel(classId, userId, requestId);

		return ResponseDTO.<ClassUserRequestIdResponseDto>builder()
				.success(true)
				.message("Cancel join class request successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassSecurity.adminOnly(#classId)")
	@GetMapping("/{classId}")
	public ResponseDTO<List<ClassUserRequestResponseDto>> getListByClass(
			@PathVariable Long classId
	) {
		List<ClassUserRequestResponseDto> response = classUserRequestService.getListByClass(classId);

		return ResponseDTO.<List<ClassUserRequestResponseDto>>builder()
				.success(true)
				.message("Get join class request list by class successful")
				.data(response)
				.build();
	}

	@Public
	@GetMapping
	public ResponseDTO<List<ClassUserRequestResponseDto>> getListByCreator() {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		List<ClassUserRequestResponseDto> response = classUserRequestService.getListByCreator(userId);

		return ResponseDTO.<List<ClassUserRequestResponseDto>>builder()
				.success(true)
				.message("Get join class request list by creator successful")
				.data(response)
				.build();
	}

}