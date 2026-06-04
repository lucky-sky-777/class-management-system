package com.mezon.classmanagement.backend.domain.auth.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
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
import com.mezon.classmanagement.backend.domain.auth.oauth2.entity.OAuthUser;
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

	/**
	 * SignIn
	 */

	public SignInResponseDto signInInternal(SignInRequestDto request) {
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
				= new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
		authenticationManager.authenticate(usernamePasswordAuthenticationToken);

		User user = userService.findByUsernameOrThrow(request.getUsername());

		return signIn(user.getId(), user.getPassword());
	}

	public SignInResponseDto signInGoogle(GoogleUser googleUser) {
		return signInOAuth(googleUser);
	}

	public SignInResponseDto signInMezon(MezonUser mezonUser) {
		return signInOAuth(mezonUser);
	}

	private SignInResponseDto signInOAuth(OAuthUser oAuthUser) {
		SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
				.provider(oAuthUser.getProvider())
				.providerId(oAuthUser.getSub())
				.username(oAuthUser.getCustomUsername())
				.displayName(oAuthUser.getDisplayName())
				.avatarUrl(oAuthUser.getAvatarUrl())
				.email(oAuthUser.getEmail())
				.build();

		return signIn(signUpRequest);
	}

	private SignInResponseDto signIn(SignUpRequestDto signUpRequest) {
		SignUpResponseDto signUpResponseDto = signUpOAuth(signUpRequest);

		return signIn(signUpResponseDto.getUserId(), signUpResponseDto.getUsername());
	}

	private SignInResponseDto signIn(
			Long userId,
			String username
	) {
		String accessToken = jwtService.generateAccessToken(userId, username);
		String refreshToken = jwtService.generateRefreshToken(userId, username);

		refreshTokenService.save(
				RefreshToken.builder()
						.jti(jwtService.extractJtiFromRefreshToken(refreshToken))
						.expiryDate(jwtService.extractExpiryFromRefreshToken(refreshToken))
						.build()
		);

		return SignInResponseDto.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	public SignUpResponseDto signUpOAuth(SignUpRequestDto request) {
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

	public SignOutResponseDto signOut(
			Authentication authentication,
			String rawRefreshToken
	) {
		try {
			Jwt refreshTokenJwt = null;
			if (rawRefreshToken != null && !rawRefreshToken.isEmpty()) {
				refreshTokenJwt = jwtService.extractJwtFromRefreshToken(rawRefreshToken);
				refreshTokenService.revokeByJti(jwtService.extractJti(refreshTokenJwt));
			}

			Jwt accessTokenJwt = jwtService.extractJwt(authentication);

			InvalidatedAccessToken newInvalidatedAccessToken = InvalidatedAccessToken.builder()
					.jti(jwtService.extractJti(accessTokenJwt))
					.expiryDate(jwtService.extractExpiry(accessTokenJwt))
					.build();
			invalidatedAccessTokenService.save(newInvalidatedAccessToken);

			if (refreshTokenJwt == null) {
				return SignOutResponseDto.builder()
						.signedOutAccessToken(jwtService.extractToken(accessTokenJwt))
						.build();
			} else {
				return SignOutResponseDto.builder()
						.signedOutAccessToken(jwtService.extractToken(accessTokenJwt))
						.signedOutRefreshToken(jwtService.extractToken(refreshTokenJwt))
						.build();
			}
		} catch (Exception e) {
			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, "Internal server error");
		}
	}

	public Authentication getAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!(authentication instanceof JwtAuthenticationToken)) {
			throw new GlobalException(GlobalException.Type.INVALID_AUTHENTICATION, "Unauthorized");
		}
		String jti = jwtService.extractJti(authentication);
		if (invalidatedAccessTokenService.isInvalidated(jti)) {
			throw new GlobalException(GlobalException.Type.INVALID_AUTHENTICATION, "Unauthorized");
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
	public SignInResponseDto refresh(String rawRefreshToken) {
		String jti = jwtService.extractJtiFromRefreshToken(rawRefreshToken);

		refreshTokenService.throwIfIsRevoked(jti);
		refreshTokenService.throwIfIsExpired(jti);

		refreshTokenService.revokeByJti(jti);

		Long userId = jwtService.extractUserIdFromRefreshToken(rawRefreshToken);
		String username = jwtService.extractUsernameFromRefreshToken(rawRefreshToken);

		String newAccessToken = jwtService.generateAccessToken(userId, username);
		String newRefreshToken = jwtService.generateRefreshToken(userId, username);

		RefreshToken refreshToken = RefreshToken.builder()
				.jti(jwtService.extractJtiFromRefreshToken(newRefreshToken))
				.expiryDate(jwtService.extractExpiryFromRefreshToken(newRefreshToken))
				.build();

		refreshTokenService.save(refreshToken);

		return SignInResponseDto.builder()
				.accessToken(newAccessToken)
				.refreshToken(newRefreshToken)
				.build();
	}
	/*
	noinspection
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
	noinspection
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