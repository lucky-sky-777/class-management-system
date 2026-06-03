package com.mezon.classmanagement.backend.config;

import com.mezon.classmanagement.backend.common.constant.ClientConstant;
import com.mezon.classmanagement.backend.common.constant.JwtConstant;
import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.exeption.custom.CustomAccessDeniedHandler;
import com.mezon.classmanagement.backend.common.exeption.custom.CustomAuthenticationEntryPoint;
import com.mezon.classmanagement.backend.common.security.validator.AccessTokenValidator;
import com.mezon.classmanagement.backend.common.security.validator.RefreshTokenValidator;
import com.mezon.classmanagement.backend.domain.auth.service.impl.UserDetailsServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

	UserDetailsServiceImpl userDetailsService;
	CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	CustomAccessDeniedHandler customAccessDeniedHandler;

	AccessTokenValidator accessTokenValidator;
	RefreshTokenValidator refreshTokenValidator;

	JwtConstant jwtConstant;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
		httpSecurity
				.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors
						.configurationSource(
								corsConfigurationSource()
						)
				)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(customAuthenticationEntryPoint)
						.accessDeniedHandler(customAccessDeniedHandler)
				)
				.oauth2ResourceServer(oauth2 -> oauth2
						.authenticationEntryPoint(customAuthenticationEntryPoint)
						//.jwt(Customizer.withDefaults())
						.jwt(jwt -> jwt
								.decoder(
										accessTokenDecoder()
								)
						)
				)
				.authorizeHttpRequests(authorize -> authorize

						/// AuthController

						.requestMatchers(HttpMethod.POST, "/api/auth/signin").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/signout").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()

						.requestMatchers(HttpMethod.GET, "/api/auth/user").authenticated()
						.requestMatchers(HttpMethod.GET, "/api/auth/state").authenticated()

						/// OAuthController

						.requestMatchers(HttpMethod.POST, "/api/auth/google/signin").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/mezon/signin").permitAll()

						.requestMatchers(HttpMethod.POST, "/api/auth/google/exchange").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/mezon/exchange").permitAll()

						.requestMatchers(HttpMethod.GET, "/api/auth/google/callback").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/auth/mezon/callback").permitAll()

						/// Public

						.requestMatchers("/api/public/**").permitAll()

						// test
						//.requestMatchers("/**").permitAll()

						// actuator
						//.requestMatchers("/actuator/**").permitAll()

						.anyRequest().authenticated()
				);

		return httpSecurity.build();
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		return new ProviderManager(daoAuthenticationProvider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

	@Bean(name = "accessTokenDecoder")
	public JwtDecoder accessTokenDecoder() {
		return jwtDecoder(accessTokenValidator);
	}

	@Bean(name = "refreshTokenDecoder")
	public JwtDecoder refreshTokenDecoder() {
		return jwtDecoder(refreshTokenValidator);
	}

	private JwtDecoder jwtDecoder(OAuth2TokenValidator<Jwt> oAuth2TokenValidator) {
		SecretKeySpec secretKeySpec = new SecretKeySpec(jwtConstant.SIGNER_KEY.getBytes(), "HmacSHA512");

		NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder
				.withSecretKey(secretKeySpec)
				.macAlgorithm(MacAlgorithm.HS512)
				.build();

		OAuth2TokenValidator<Jwt> validator =
				new DelegatingOAuth2TokenValidator<>(
						JwtValidators.createDefault(),
						oAuth2TokenValidator
				);

		nimbusJwtDecoder.setJwtValidator(validator);

		return nimbusJwtDecoder;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();

		corsConfiguration.setAllowedOrigins(ClientConstant.ALLOWED_ORIGIN_LIST);
		corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		corsConfiguration.setAllowedHeaders(List.of("*"));
		corsConfiguration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

		return urlBasedCorsConfigurationSource;
	}

}