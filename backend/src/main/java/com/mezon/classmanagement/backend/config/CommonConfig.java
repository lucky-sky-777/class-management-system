package com.mezon.classmanagement.backend.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
public class CommonConfig {

	@Bean
	public ObjectMapper objectMapper() {
		return JsonMapper.builder()
				.addModule(new JavaTimeModule())
				.configure(
						DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
						false
				)
				.build();
	}

	@Bean
	public HttpClient httpClient() {
		return HttpClient.newHttpClient();
	}

}