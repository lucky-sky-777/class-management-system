package com.mezon.classmanagement.backend.domain.auth.oauth2.controller;

import com.mezon.classmanagement.backend.common.constant.ClientConstant;
import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain.auth.dto.signin.SignInResponseDto;
import com.mezon.classmanagement.backend.domain.auth.oauth2.dto.ExchangeOAuthAuthorizationCodeRequest;
import com.mezon.classmanagement.backend.domain.auth.oauth2.entity.OAuthAuthorization;
import com.mezon.classmanagement.backend.domain.auth.oauth2.factory.OAuthFactory;
import com.mezon.classmanagement.backend.domain.auth.oauth2.service.GoogleOAuthService;
import com.mezon.classmanagement.backend.domain.auth.oauth2.service.MezonOAuthService;
import com.mezon.classmanagement.backend.domain.auth.oauth2.service.OAuthAuthorizationService;
import com.mezon.classmanagement.backend.domain.auth.oauth2.strategy.OAuthStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/auth/{provider}")
@RestController
public class OAuthController {

	OAuthFactory oAuthFactory;

	OAuthAuthorizationService oAuthAuthorizationService;

	GoogleOAuthService googleOAuthService;
	MezonOAuthService mezonOAuthService;

	@PostMapping("/signin")
	public void signin(
			@PathVariable String provider,
			HttpServletRequest request,
			HttpServletResponse response
	) throws IOException {
		OAuthStrategy strategy = oAuthFactory.getStrategy(provider);

		String origin = request.getHeader("Origin");
		if (!ClientConstant.ALLOWED_ORIGIN_LIST.contains(origin)) {
			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Invalid request");
		}

		String url = strategy.getAuthUrl(origin);

		response.sendRedirect(url);
	}

	@GetMapping("/callback")
	public void callback(
			@PathVariable String provider,
			@RequestParam("code") String code,
			/* Client URL */
			@RequestParam(value = "state", required = false) String state,
			HttpServletResponse response
	) {
		OAuthStrategy strategy = oAuthFactory.getStrategy(provider);

		SignInResponseDto signInResponseDto;

		String clientRedirectUrl;

		try {
			String accessToken = null;

			if (OAuthStrategy.MEZON.equals(strategy.getName())) {
				accessToken = mezonOAuthService.exchangeCodeForToken(code, state);

			}
			if (OAuthStrategy.GOOGLE.equals(strategy.getName())) {
				accessToken = googleOAuthService.exchangeCodeForToken(code);
			}

			signInResponseDto = strategy.auth(accessToken);
			String oauthAuthorizationCode = UUID.randomUUID().toString();

			Assert.notNull(signInResponseDto, "Internal server error");

			OAuthAuthorization oAuthAuthorization = oAuthAuthorizationService.create(
					oauthAuthorizationCode,
					provider,
					signInResponseDto.getAccessToken(),
					signInResponseDto.getRefreshToken()
			);

			clientRedirectUrl =
					state
							+ "/oauth2-signin-redirect?authorization-code="
							+ oAuthAuthorization.getCode()
							+ "&provider="
							+ provider;
		} catch (Exception e) {
			clientRedirectUrl =
					state
							+ "/oauth2-signin-redirect?error-message="
							+ e.getMessage();
		}

		try {
			response.sendRedirect(clientRedirectUrl);
		} catch (IOException ie) {
			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, "Internal server error");
		}
	}

	@PostMapping("/exchange")
	public ResponseDTO<SignInResponseDto> exchange(
			@PathVariable String provider,
			@RequestBody ExchangeOAuthAuthorizationCodeRequest request
	) {
		OAuthStrategy oAuthStrategy = oAuthFactory.getStrategy(provider);

		OAuthAuthorization oAuthAuthorization = oAuthAuthorizationService.exchange(oAuthStrategy.getName(), request);
		SignInResponseDto response = SignInResponseDto.builder()
				.accessToken(oAuthAuthorization.getAccessToken())
				.refreshToken(oAuthAuthorization.getRefreshToken())
				.build();

		return ResponseDTO.ok(
				"Exchange oauth authorization code successful",
				response
		);
	}

}