package com.mezon.classmanagement.backend.domain.auth.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
import com.mezon.classmanagement.backend.domain.auth.dto.signin.SignInRequestDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signin.SignInResponseDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signout.SignOutRequestDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signout.SignOutResponseDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signup.SignUpRequestDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signup.SignUpResponseDto;
import com.mezon.classmanagement.backend.domain.auth.dto.user.UserResponseDto;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.InvalidatedTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

	AuthService authService;
	JwtService jwtService;
	InvalidatedTokenService invalidatedTokenService;

	@PostMapping("/signin")
	public ResponseDTO<SignInResponseDto> signIn(@RequestBody SignInRequestDto request){
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

	@PostMapping("/signout")
	public ResponseDTO<SignOutResponseDto> signOut() {
		Authentication authentication = authService.getAuthentication();
		SignOutResponseDto signOutResponseDto = authService.signOut(authentication);

		return ResponseDTO.<SignOutResponseDto>builder()
				.success(true)
				.message("Sign out successful")
				.data(signOutResponseDto)
				.build();
	}

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

	@PostMapping("/state")
	public ResponseDTO<Void> validateAuthState() {
		Authentication authentication = authService.getAuthentication();
		String jti = jwtService.extractJti(authentication);

		if (invalidatedTokenService.isInvalidated(jti)) {
			throw new AuthenticationCredentialsNotFoundException("No authentication");
		}

		return ResponseDTO.<Void>builder()
				.success(true)
				.message("Valid auth state")
				.build();
	}

}