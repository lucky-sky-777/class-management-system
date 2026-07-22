package com.mezon.classmanagement.backend_document;

import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class GatewayHeaderAuthenticationFilter extends OncePerRequestFilter {

	private static final String USER_ID_HEADER = "X-User-Id";
	private static final String USERNAME_HEADER = "X-Username";

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			@Nullable HttpServletResponse response,
			@Nullable FilterChain filterChain
	) throws ServletException, IOException {
		String userId = request.getHeader(USER_ID_HEADER);
		String username = request.getHeader(USERNAME_HEADER);

		if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			GatewayUser principal = new GatewayUser(Long.valueOf(userId), username);

			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());

			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		if (filterChain != null) {
			filterChain.doFilter(request, response);
		}
	}

	public record GatewayUser(Long id, String username) {
	}

}