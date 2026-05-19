package com.mezon.classmanagement.backend.common.security.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.security.dto.PermissionResponseDto;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
import com.mezon.classmanagement.backend.common.security.service.PermissionService;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
@RestController
public class PermissionController {

	AuthService authService;
	JwtService jwtService;

	PermissionService permissionService;

	@PreAuthorize("@ClassPermission.adminOnly(#classId)")
	@PutMapping("/classes/{classId}")
	public ResponseDTO<Void> setPermissions(
			@PathVariable Long classId
	) {
		return ResponseDTO.ok("ok");
	}

	@GetMapping
	public ResponseDTO<List<PermissionResponseDto>> getList(
	) {
		List<PermissionResponseDto> response = permissionService.getPermissionList();

		return ResponseDTO.ok(
				"Get permission list successful",
				response
		);
	}

}