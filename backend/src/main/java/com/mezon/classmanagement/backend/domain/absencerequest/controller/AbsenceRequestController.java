package com.mezon.classmanagement.backend.domain.absencerequest.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
import com.mezon.classmanagement.backend.domain.absencerequest.dto.request.CreateAbsenceRequestRequestDto;
import com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestIdResponseDto;
import com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestResponseDto;
import com.mezon.classmanagement.backend.domain.absencerequest.service.AbsenceRequestService;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/absence-requests")
@RestController
public class AbsenceRequestController {

	AuthService authService;
	JwtService jwtService;

	AbsenceRequestService absenceRequestService;

	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@PostMapping("/classes/{classId}")
	public ResponseDTO<AbsenceRequestResponseDto> create(
			@PathVariable Long classId,
			@RequestBody CreateAbsenceRequestRequestDto request
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		AbsenceRequestResponseDto response = absenceRequestService.create(classId, userId, request);

		return ResponseDTO.ok(
				"Create absence request successful",
				response
		);
	}

	@PreAuthorize("@ClassSecurity.manageAbsenceRequest(#classId)")
	@PostMapping("/classes/{classId}/requests/{requestId}/approve")
	public ResponseDTO<AbsenceRequestIdResponseDto> approve(
			@PathVariable Long classId,
			@PathVariable Long requestId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		AbsenceRequestIdResponseDto response = absenceRequestService.approve(classId, userId, requestId);

		return ResponseDTO.ok(
				"Approve absence request successful",
				response
		);
	}

	@PreAuthorize("@ClassSecurity.manageAbsenceRequest(#classId)")
	@PostMapping("/classes/{classId}/requests/{requestId}/reject")
	public ResponseDTO<AbsenceRequestIdResponseDto> reject(
			@PathVariable Long classId,
			@PathVariable Long requestId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		AbsenceRequestIdResponseDto response = absenceRequestService.reject(classId, userId, requestId);

		return ResponseDTO.ok(
				"Reject absence request successful",
				response
		);
	}

	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@PatchMapping("/classes/{classId}/requests/{requestId}/cancel")
	public ResponseDTO<AbsenceRequestIdResponseDto> cancel(
			@PathVariable Long classId,
			@PathVariable Long requestId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		AbsenceRequestIdResponseDto response = absenceRequestService.cancel(classId, userId, requestId);

		return ResponseDTO.ok(
				"Cancel absence request successful",
				response
		);
	}

	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@GetMapping("/classes/{classId}/requests")
	public ResponseDTO<List<AbsenceRequestResponseDto>> getListByClass(@PathVariable Long classId) {
		return ResponseDTO.<List<AbsenceRequestResponseDto>>builder()
				.success(true)
				.message("Get absence request list by class successful")
				.data(absenceRequestService.getListByClass(classId))
				.build();
	}

	@Deprecated
	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@GetMapping("/users/{userId}/requests")
	public ResponseDTO<List<AbsenceRequestResponseDto>> getListByClassAndCreator(
			@PathVariable Long classId,
			@PathVariable Long userId
	) {
		return ResponseDTO.<List<AbsenceRequestResponseDto>>builder()
				.success(true)
				.message("Get absence request list by class and creator successful")
				.data(absenceRequestService.getListByClassAndCreator(1L, userId))
				.build();
	}

}