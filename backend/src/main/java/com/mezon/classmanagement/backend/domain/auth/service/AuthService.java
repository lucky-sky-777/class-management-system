package com.mezon.classmanagement.backend.domain.auth.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
import com.mezon.classmanagement.backend.common.util.EmailProcessor;
import com.mezon.classmanagement.backend.domain.auth.dto.signin.SignInRequestDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signin.SignInResponseDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signout.SignOutResponseDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signup.SignUpRequestDto;
import com.mezon.classmanagement.backend.domain.auth.dto.signup.SignUpResponseDto;
import com.mezon.classmanagement.backend.domain.auth.dto.user.UserResponseDto;
import com.mezon.classmanagement.backend.domain.auth.entity.InvalidatedToken;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.auth.mapper.UserMapper;
import com.mezon.classmanagement.backend.domain.auth.oauth2.entity.GoogleUser;
import com.mezon.classmanagement.backend.domain.auth.oauth2.entity.MezonUser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class AuthService {

	AuthenticationManager authenticationManager;
	UserService userService;
	UserMapper userMapper;
	JwtService jwtService;
	InvalidatedTokenService invalidatedTokenService;

	public SignInResponseDto signInInternal(SignInRequestDto request) {
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
				= new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
		authenticationManager.authenticate(usernamePasswordAuthenticationToken);

		User user = userService.findByUsernameOrThrow(request.getUsername());

		String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername());
		String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());

		return SignInResponseDto.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	public SignInResponseDto signInMezon(MezonUser mezonUser) {
		String username = EmailProcessor.extractAndClean(mezonUser.getEmail()) + "-" + User.Provider.MEZON + "-" + System.currentTimeMillis();

		SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
				.provider(User.Provider.MEZON)
				.providerId(mezonUser.getSub())
				.username(username)
				.displayName(mezonUser.getDisplayName())
				.avatarUrl(mezonUser.getAvatar())
				.email(mezonUser.getEmail())
				.build();
		SignUpResponseDto signUpResponse = signUpExternal(signUpRequest);

		String accessToken = jwtService.generateAccessToken(signUpResponse.getUserId(), signUpResponse.getUsername());
		String refreshToken = jwtService.generateRefreshToken(signUpResponse.getUserId(), signUpResponse.getUsername());

		return SignInResponseDto.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	public SignInResponseDto signInGoogle(GoogleUser googleUser) {
		String username = EmailProcessor.extractAndClean(googleUser.getEmail()) + "-" + User.Provider.GOOGLE + "-" + System.currentTimeMillis();

		SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
				.provider(User.Provider.GOOGLE)
				.providerId(googleUser.getSub())
				.username(username)
				.displayName(googleUser.getDisplayName())
				.avatarUrl(googleUser.getAvatarUrl())
				.email(googleUser.getEmail())
				.build();
		SignUpResponseDto signUpResponse = signUpExternal(signUpRequest);

		String accessToken = jwtService.generateAccessToken(signUpResponse.getUserId(), signUpResponse.getUsername());
		String refreshToken = jwtService.generateRefreshToken(signUpResponse.getUserId(), signUpResponse.getUsername());

		return SignInResponseDto.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	public SignUpResponseDto signUpExternal(SignUpRequestDto request) {
		if (userService.existsByProviderId(request.getProviderId())) {
			User currentUser = userService.findByProviderIdOrThrow(request.getProviderId());

			return SignUpResponseDto.builder()
					.userId(currentUser.getId())
					.username(currentUser.getUsername())
					.build();
		}

		return signUp(request);
	}

	public SignUpResponseDto signUpInternal(SignUpRequestDto request) {
		userService.throwIfExistsByUsername(request.getUsername());
		return signUp(request);
	}

	private SignUpResponseDto signUp(SignUpRequestDto request) {
		User newUser = userService.createUser(request);

		return SignUpResponseDto.builder()
				.userId(newUser.getId())
				.username(newUser.getUsername())
				.build();
	}

	public SignOutResponseDto signOut(Authentication authentication) {
		try {
			Jwt jwt = jwtService.getJwt(authentication);

			InvalidatedToken newInvalidatedToken = InvalidatedToken.builder()
					.jti(jwt.getId())
					.expiryDate(jwt.getExpiresAt())
					.build();
			invalidatedTokenService.save(newInvalidatedToken);

			return SignOutResponseDto.builder()
					.signedOutAccessToken(jwt.getTokenValue())
					.build();
		} catch (Exception e) {
			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, "Internal server error");
		}
	}

	public Authentication getAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!(authentication instanceof JwtAuthenticationToken)) {
			throw new GlobalException(GlobalException.Type.INVALID_AUTHENTICATION, "Invalid authentication");
		}

		return authentication;
	}

	@Transactional
	public UserResponseDto getCurrentUser(Authentication authentication) {
		Long userId = jwtService.extractUserId(authentication);
		User user = userService.findByUserIdOrThrow(userId);
		return userMapper.toUserResponseDto(user);
	}

	/*
	no
	@Deprecated
	public SignOutResponseDto signOut(SignOutRequestDto request) {
		try {
			SignedJWT signedJWT = verifyToken(request.getAccessToken());

			String jti = signedJWT.getJWTClaimsSet().getJWTID();
			Date expiryDate = signedJWT.getJWTClaimsSet().getExpirationTime();

			InvalidatedToken newInvalidatedToken = InvalidatedToken.builder()
					.jti(jti)
					.expiryDate(expiryDate.toInstant())
					.build();
			InvalidatedToken responseInvalidatedToken = invalidatedTokenService.save(newInvalidatedToken);

			return SignOutResponseDto.builder()
					.invalidatedToken(request.getAccessToken())
					.build();
		} catch (Exception e) {
			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, "Internal server error");
		}
	}
	*/

	/*
	no
	@Deprecated
	private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(JwtConstant.SIGNER_KEY.getBytes());

		SignedJWT signedJWT = SignedJWT.parse(token);

		Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		var verified = signedJWT.verify(verifier);

		if (!(verified && expiryTime.after(new Date()))) {
			//Exception throw
			return null;
		}

		return signedJWT;
	}
	*/

}