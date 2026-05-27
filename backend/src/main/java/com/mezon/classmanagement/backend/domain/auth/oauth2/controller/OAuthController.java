package com.mezon.classmanagement.backend.domain.auth.oauth2.controller;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain.auth.dto.signin.SignInResponseDto;
import com.mezon.classmanagement.backend.domain.auth.oauth2.factory.OAuthFactory;
import com.mezon.classmanagement.backend.domain.auth.oauth2.service.GoogleOAuthService;
import com.mezon.classmanagement.backend.domain.auth.oauth2.service.MezonOAuthService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/auth/{provider}")
@RestController
public class OAuthController {

	OAuthFactory oauthFactory;

	GoogleOAuthService googleOAuthService;
	MezonOAuthService mezonOAuthService;

	@PostMapping("/signin")
	public void signin(
			@PathVariable String provider,
			HttpServletRequest request,
			HttpServletResponse response
	) throws IOException {
		OAuthStrategy strategy = oauthFactory.getStrategy(provider);

		String origin = request.getHeader("Origin");
		String url = strategy.getAuthUrl(origin);

		response.sendRedirect(url);
	}

	@GetMapping("/callback")
	public void callback(
			@PathVariable String provider,
			@RequestParam("code") String code,
			@RequestParam(value = "state", required = false) String state,
			HttpServletResponse response
	) {
		OAuthStrategy strategy = oauthFactory.getStrategy(provider);

		SignInResponseDto signInResponseDto = null;

		String clientUrl = state;
		String clientRedirectUrl = null;

		try {
			String accessToken = null;

			if (OAuthStrategy.MEZON.equals(strategy.getName())) {
				accessToken = mezonOAuthService.exchangeCodeForToken(code, state);

			}
			if (OAuthStrategy.GOOGLE.equals(strategy.getName())) {
				accessToken = googleOAuthService.exchangeCodeForToken(code);
			}

			signInResponseDto = strategy.auth(accessToken);

			Assert.notNull(signInResponseDto, "Internal server error");

			clientRedirectUrl =
					clientUrl
							+ "/oauth2-signin-redirect?access-token="
							+ signInResponseDto.getAccessToken()
							+ "&refresh-token="
							+ signInResponseDto.getRefreshToken();
		} catch (Exception e) {
			clientRedirectUrl =
					clientUrl
							+ "/oauth2-signin-redirect?error-message="
							+ e.getMessage();
		}

		try {
			response.sendRedirect(clientRedirectUrl);
		} catch (IOException ie) {
			throw new RuntimeException();
		}
	}

}