package com.mezon.classmanagement.backend.domain.activity.activityregistration.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.dto.response.ActivityRegistrationIdResponseDto;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.dto.response.ActivityRegistrationResponseDto;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.service.ActivityRegistrationService;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/classes/{classId}/activities/{activityId}/registrations")
@RestController
public class ActivityRegistrationController {

	AuthService authService;
	JwtService jwtService;

	ActivityRegistrationService activityRegistrationService;

	@PostMapping
	public ResponseDTO<ActivityRegistrationResponseDto> create(
			@PathVariable Long classId,
			@PathVariable Long activityId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserId(authentication);

		ActivityRegistrationResponseDto response = activityRegistrationService.create(classId, activityId, userId);

		return ResponseDTO.ok(
				"Create activity registration successful",
				response
		);
	}

	@GetMapping
	public ResponseDTO<List<ActivityRegistrationResponseDto>> getByClassAndActivity(
			@PathVariable Long classId,
			@PathVariable Long activityId
	) {
		List<ActivityRegistrationResponseDto> response = activityRegistrationService.getByClassAndActivity(classId, activityId);

		return ResponseDTO.<List<ActivityRegistrationResponseDto>>builder()
				.success(true)
				.message("Get activity registrations successful")
				.data(response)
				.build();
	}

	@PatchMapping("/{activityRegistrationId}/approve")
	public ResponseDTO<ActivityRegistrationIdResponseDto> approve(
			@PathVariable Long classId,
			@PathVariable Long activityId,
			@PathVariable Long activityRegistrationId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserId(authentication);

		ActivityRegistrationIdResponseDto response = activityRegistrationService.approve(classId, activityId, userId, activityRegistrationId);

		return ResponseDTO.<ActivityRegistrationIdResponseDto>builder()
				.success(true)
				.message("Approve activity registration successful")
				.data(response)
				.build();
	}

	@PatchMapping("/{activityRegistrationId}/reject")
	public ResponseDTO<ActivityRegistrationIdResponseDto> reject(
			@PathVariable Long classId,
			@PathVariable Long activityId,
			@PathVariable Long activityRegistrationId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserId(authentication);

		ActivityRegistrationIdResponseDto response = activityRegistrationService.reject(classId, activityId, userId, activityRegistrationId);

		return ResponseDTO.<ActivityRegistrationIdResponseDto>builder()
				.success(true)
				.message("Reject activity registration successful")
				.data(response)
				.build();
	}

	@PatchMapping("/{activityRegistrationId}/cancel")
	public ResponseDTO<ActivityRegistrationIdResponseDto> cancel(
			@PathVariable Long classId,
			@PathVariable Long activityId,
			@PathVariable Long activityRegistrationId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserId(authentication);

		ActivityRegistrationIdResponseDto response = activityRegistrationService.cancel(classId, activityId, userId, activityRegistrationId);

		return ResponseDTO.<ActivityRegistrationIdResponseDto>builder()
				.success(true)
				.message("Cancel activity registration successful")
				.data(response)
				.build();
	}

}