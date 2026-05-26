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
import com.mezon.classmanagement.backend.domain.auth.entity.InvalidatedAccessToken;
import com.mezon.classmanagement.backend.domain.auth.entity.RefreshToken;
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
	InvalidatedAccessTokenService invalidatedAccessTokenService;
	RefreshTokenService refreshTokenService;

	public SignInResponseDto signInInternal(SignInRequestDto request) {
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
				= new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
		authenticationManager.authenticate(usernamePasswordAuthenticationToken);

		User user = userService.findByUsernameOrThrow(request.getUsername());

		String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername());
		String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());

		refreshTokenService.save(RefreshToken.builder()
				.jti(jwtService.extractJti(refreshToken))
				.expiryDate(jwtService.extractExpiry(refreshToken))
				.build());

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

		refreshTokenService.save(RefreshToken.builder()
				.jti(jwtService.extractJti(refreshToken))
				.expiryDate(jwtService.extractExpiry(refreshToken))
				.build());

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

		refreshTokenService.save(RefreshToken.builder()
				.jti(jwtService.extractJti(refreshToken))
				.expiryDate(jwtService.extractExpiry(refreshToken))
				.build());

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

	public SignOutResponseDto signOut(Authentication authentication, String rawRefreshToken) {
		try {
			Jwt refreshTokenJwt = null;
			if (rawRefreshToken != null && !rawRefreshToken.isEmpty()) {
				refreshTokenJwt = jwtService.parse(rawRefreshToken);
				refreshTokenService.revokeByJti(jwtService.extractJti(refreshTokenJwt));
			}

			Jwt accessTokenJwt = jwtService.getJwt(authentication);

			InvalidatedAccessToken newInvalidatedAccessToken = InvalidatedAccessToken.builder()
					.jti(jwtService.extractJti(accessTokenJwt))
					.expiryDate(jwtService.extractExpiry(accessTokenJwt))
					.build();
			invalidatedAccessTokenService.save(newInvalidatedAccessToken);

			return SignOutResponseDto.builder()
					.signedOutAccessToken(jwtService.extractToken(accessTokenJwt))
					.signedOutRefreshToken(jwtService.extractToken(refreshTokenJwt))
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


	@Transactional
	public SignInResponseDto refresh2(String rawRefreshToken) {
		// Parse và validate refresh token
		Jwt jwt = jwtService.parse(rawRefreshToken);
		String jti = jwt.getId();

		// Kiểm tra token đã bị revoke chưa
		if (refreshTokenService.isRevoked(jti)) {
			throw new GlobalException(GlobalException.Type.INVALID_AUTHENTICATION, "Refresh token revoked");
		}

		// Rotation: revoke token cũ
		refreshTokenService.revokeByJti(jti);

		// Lấy thông tin user từ token cũ
		Long userId = jwtService.extractUserId(jwt);
		String username = jwtService.extractUsername(jwt);

		// Tạo token mới
		String newAccessToken = jwtService.generateAccessToken(userId, username);
		String newRefreshToken = jwtService.generateRefreshToken(userId, username);

		// Lưu refresh token mới vào DB
		refreshTokenService.save(RefreshToken.builder()
				.jti(jwtService.extractJti(newRefreshToken))
				.expiryDate(jwtService.extractExpiry(newRefreshToken))
				.revoked(false)
				.build());

		return SignInResponseDto.builder()
				.accessToken(newAccessToken)
				.refreshToken(newRefreshToken)
				.build();
	}

	@Transactional
	public SignInResponseDto refresh(String rawRefreshToken) {
		String jti = jwtService.extractJti(rawRefreshToken);

		refreshTokenService.throwIfIsRevoked(jti);

		refreshTokenService.revokeByJti(jti);

		Long userId = jwtService.extractUserId(rawRefreshToken);
		String username = jwtService.extractUsername(rawRefreshToken);

		String newAccessToken = jwtService.generateAccessToken(userId, username);
		String newRefreshToken = jwtService.generateRefreshToken(userId, username);

		RefreshToken refreshToken = RefreshToken.builder()
				.jti(jwtService.extractJti(newRefreshToken))
				.expiryDate(jwtService.extractExpiry(newRefreshToken))
				.build();

		refreshTokenService.save(refreshToken);

		return SignInResponseDto.builder()
				.accessToken(newAccessToken)
				.refreshToken(newRefreshToken)
				.build();
	}
	/*
	no
	@Deprecated
	public SignOutResponseDto signOut(SignOutRequestDto request) {
		try {
			SignedJWT signedJWT = verifyToken(request.getAccessToken());

			String jti = signedJWT.getJWTClaimsSet().getJWTID();
			Date expiryDate = signedJWT.getJWTClaimsSet().getExpirationTime();

			InvalidatedAccessToken newInvalidatedToken = InvalidatedAccessToken.builder()
					.jti(jti)
					.expiryDate(expiryDate.toInstant())
					.build();
			InvalidatedAccessToken responseInvalidatedToken = invalidatedTokenService.save(newInvalidatedToken);

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