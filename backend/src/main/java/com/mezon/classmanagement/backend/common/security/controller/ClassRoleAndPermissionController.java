package com.mezon.classmanagement.backend.common.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.security.dto.ClassRoleAndPermissionListResponseDto;
import com.mezon.classmanagement.backend.common.security.service.ClassRoleAndPermissionService;
import com.mezon.classmanagement.backend.domain.classuser.dto.ClassUserResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.dto.UpdateClassUserRoleAndPermissionRequestDto;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/classes/{classId}/authorities")
@RestController
public class ClassRoleAndPermissionController {

	ClassRoleAndPermissionService classRoleAndPermissionService;

	@PreAuthorize("@ClassSecurity.adminOnly(#classId)")
	@PutMapping("/members/{userId}")
	public ResponseDTO<ClassUserResponseDto> setRoleAndPermissions(
			@PathVariable Long classId,
			@PathVariable Long userId,
			@RequestBody UpdateClassUserRoleAndPermissionRequestDto request
	) {
		ClassUserResponseDto response = classRoleAndPermissionService.setClassUserRoleAndPermission(classId, userId, request);
		return ResponseDTO.ok(
				"Update class user role and permissions successful",
				response
		);
	}

	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@GetMapping
	public ResponseDTO<ClassRoleAndPermissionListResponseDto> getClassRoleAndPermissionList(
			@PathVariable Long classId
	) {
		ClassRoleAndPermissionListResponseDto response = classRoleAndPermissionService.getRoleAndPermissionList();

		return ResponseDTO.ok(
				"Get class role and permission list successful",
				response
		);
	}

}