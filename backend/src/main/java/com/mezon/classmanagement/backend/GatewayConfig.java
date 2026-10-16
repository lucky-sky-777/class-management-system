package com.mezon.classmanagement.backend;

import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

//	private static final String DOCUMENT_SERVER = "http://localhost:9090";
//
//	private static final String USER_ID_HEADER = "X-User-Id";
//	private static final String USERNAME_HEADER = "X-Username";
//
//	@Bean
//	public RouterFunction<ServerResponse> documentRoute(JwtService jwtService) {
//		return route("document-server")
//				.route(
//						path("/api/documents/**"),
//						http()
//				)
//				.before(request -> {
//					ServerRequest.Builder requestBuilder = ServerRequest.from(request);
//
//					requestBuilder.headers(headers -> {
//						headers.remove(USER_ID_HEADER);
//						headers.remove(USERNAME_HEADER);
//					});
//
//					Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//					if (
//							authentication != null
//									&& authentication.isAuthenticated()
//									&& (authentication.getPrincipal() instanceof Jwt jwt)
//					) {
//						requestBuilder.headers(headers -> {
//							headers.set(USER_ID_HEADER, String.valueOf(jwtService.extractUserIdFromJwt(jwt)));
//							headers.set(USERNAME_HEADER, jwtService.extractUsernameFromJwt(jwt));
//						});
//					}
//
//					ServerRequest modifiedRequest = requestBuilder.build();
//
//					URI targetUri = UriComponentsBuilder.fromUriString(DOCUMENT_SERVER)
//							.path(request.uri().getRawPath())
//							.query(request.uri().getRawQuery())
//							.build(true)
//							.toUri();
//
//					MvcUtils.setRequestUrl(modifiedRequest, targetUri);
//
//					return modifiedRequest;
//				})
//				.build();
//	}
}