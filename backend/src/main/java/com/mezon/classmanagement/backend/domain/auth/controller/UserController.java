package com.mezon.classmanagement.backend.domain.auth.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.auth.dto.user.UserResponseDto;
import com.mezon.classmanagement.backend.domain.auth.dto.user.request.UpdateUserRequestDto;
import com.mezon.classmanagement.backend.domain.auth.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

	UserService userService;

	@PatchMapping("/{userId}")
	public ResponseDTO<UserResponseDto> update(
			@PathVariable Long userId,
			@RequestBody UpdateUserRequestDto request
	) {
		UserResponseDto response = userService.update(userId, request);

		return ResponseDTO.ok(
				"Update user successful",
				response
		);
	}

}