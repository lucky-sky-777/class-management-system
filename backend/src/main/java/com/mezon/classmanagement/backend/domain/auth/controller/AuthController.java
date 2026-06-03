package com.mezon.classmanagement.backend.domain.auth.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.auth.dto.signin.SignInRequestDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signin.SignInResponseDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signout.SignOutResponseDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signup.SignUpRequestDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signup.SignUpResponseDto;
import com.mezon.classmanagement.backend.domain.auth.dto.user.UserResponseDto;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

	AuthService authService;

	@PostMapping("/signin")
	public ResponseDTO<SignInResponseDto> signIn(@RequestBody SignInRequestDto request) {
		SignInResponseDto signInResponseDto = authService.signInInternal(request);

		return ResponseDTO.<SignInResponseDto>builder()
				.success(true)
				.message("Sign in successful")
				.data(signInResponseDto)
				.build();
	}

	@PostMapping("/signup")
	public ResponseDTO<SignUpResponseDto> signUp(@RequestBody SignUpRequestDto request) {
		request.setProvider(User.Provider.INTERNAL);
		request.setProviderId(null);

		SignUpResponseDto signUpResponseDto = authService.signUpInternal(request);

		return ResponseDTO.<SignUpResponseDto>builder()
				.success(true)
				.message("Sign up successful")
				.data(signUpResponseDto)
				.build();
	}

	@PreAuthorize("@TokenSecurity.isValid()")
	@PostMapping("/signout")
	public ResponseDTO<SignOutResponseDto> signOut(
			@RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken
	) {
		System.out.println(refreshToken);
		Authentication authentication = authService.getAuthentication();
		SignOutResponseDto signOutResponseDto = authService.signOut(authentication, refreshToken);

		return ResponseDTO.<SignOutResponseDto>builder()
				.success(true)
				.message("Sign out successful")
				.data(signOutResponseDto)
				.build();
	}

	@PreAuthorize("@TokenSecurity.isValid()")
	@GetMapping("/user")
	public ResponseDTO<UserResponseDto> current() {
		Authentication authentication = authService.getAuthentication();
		UserResponseDto response = authService.getCurrentUser(authentication);

		return ResponseDTO.<UserResponseDto>builder()
				.success(true)
				.message("Get current user successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@TokenSecurity.isValid()")
	@GetMapping("/state")
	public ResponseDTO<Void> validateAuthState() {
		return ResponseDTO.<Void>builder()
				.success(true)
				.message("Valid auth state")
				.build();
	}

	@PostMapping("/refresh")
	public ResponseDTO<SignInResponseDto> refresh(
			@RequestHeader(name = "X-Refresh-Token") String refreshToken
	) {
		SignInResponseDto response = authService.refresh(refreshToken);

		return ResponseDTO.<SignInResponseDto>builder()
				.success(true)
				.message("Refresh tokens successful")
				.data(response)
				.build();
	}

}