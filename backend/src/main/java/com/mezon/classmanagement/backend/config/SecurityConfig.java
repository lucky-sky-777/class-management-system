package com.mezon.classmanagement.backend.config;

import com.mezon.classmanagement.backend.common.exeption.custom.CustomAccessDeniedHandler;
import com.mezon.classmanagement.backend.common.exeption.custom.CustomAuthenticationEntryPoint;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

	CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	CustomAccessDeniedHandler customAccessDeniedHandler;

	CorsConfig corsConfig;
	JwtConfig jwtConfig;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
		httpSecurity
				.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors
						.configurationSource(
								corsConfig.corsConfigurationSource()
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
										jwtConfig.accessTokenDecoder()
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

}