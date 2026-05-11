package com.mezon.classmanagement.backend.domain.classuser.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.classuser.dto.ClassUserIdResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.dto.ClassUserResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.dto.CreateClassUserRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.dto.UpdateClassUserPermissionsRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.dto.UpdateClassUserRoleRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.entity.ClassUser;
import com.mezon.classmanagement.backend.domain.classuser.service.ClassUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/classes/{classId}/members")
@RestController
public class ClassUserController {

	ClassUserService classUserService;

	@PreAuthorize("@ClassPermission.adminOnly(#classId)")
	@PostMapping
	public ResponseDTO<ClassUserResponseDto> createClassUser(
			@PathVariable Long classId,
			@RequestBody CreateClassUserRequestDto request
	) {
		ClassUserResponseDto response = classUserService.createClassUser(classId, request, ClassUser.Role.CLASS_MEMBER);

		return ResponseDTO.<ClassUserResponseDto>builder()
				.success(true)
				.message("Create class user successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassPermission.adminOnly(#classId)")
	@PatchMapping("/{userId}/role")
	public ResponseDTO<ClassUserResponseDto> updateClassUserRole(
			@PathVariable Long classId,
			@PathVariable Long userId,
			@RequestBody UpdateClassUserRoleRequestDto request
	) {
		ClassUserResponseDto response = classUserService.updateClassUserRole(classId, userId, request);

		return ResponseDTO.<ClassUserResponseDto>builder()
				.success(true)
				.message("Update class user role successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassPermission.adminOnly(#classId)")
	@PatchMapping("/{userId}/permissions")
	public ResponseDTO<ClassUserResponseDto> updateClassUserPermissions(
			@PathVariable Long classId,
			@PathVariable Long userId,
			@RequestBody UpdateClassUserPermissionsRequestDto request
	) {
		ClassUserResponseDto response = classUserService.updateClassUserPermissions(classId, userId, request);

		return ResponseDTO.<ClassUserResponseDto>builder()
				.success(true)
				.message("Update class user permissions successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassPermission.adminOnly(#classId)")
	@DeleteMapping("/{userId}")
	public ResponseDTO<ClassUserIdResponseDto> deleteClassUser(
			@PathVariable Long classId,
			@PathVariable Long userId
	) {
		ClassUserIdResponseDto response = classUserService.deleteClassUser(classId, userId);

		return ResponseDTO.<ClassUserIdResponseDto>builder()
				.success(true)
				.message("Delete class user successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassPermission.everyoneInClass(#classId)")
	@GetMapping
	public ResponseDTO<List<ClassUserResponseDto>> getClassUsers(
			@PathVariable Long classId
	) {
		List<ClassUserResponseDto> response = classUserService.getClassUsers(classId);

		return ResponseDTO.<List<ClassUserResponseDto>>builder()
				.success(true)
				.message("Get class users successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassPermission.everyoneInClass(#classId)")
	@GetMapping("/ungrouped")
	public ResponseDTO<List<ClassUserResponseDto>> getUngroupedClassUsers(
			@PathVariable Long classId
	) {
		List<ClassUserResponseDto> response = classUserService.getUngroupedClassUsers(classId);

		return ResponseDTO.<List<ClassUserResponseDto>>builder()
				.success(true)
				.message("Get ungrouped class users successful")
				.data(response)
				.build();
	}

}