package com.mezon.classmanagement.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean(name = "vietQrWebClient")
	public WebClient vietQrWebClient() {
		return WebClient.builder()
				.baseUrl("https://api.vietqr.io")
				.build();
	}

	@Deprecated
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}