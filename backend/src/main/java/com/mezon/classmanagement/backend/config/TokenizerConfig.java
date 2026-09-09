package com.mezon.classmanagement.backend.config;

import ai.djl.sentencepiece.SpTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class TokenizerConfig {

	@Bean(destroyMethod = "close")
	public SpTokenizer spTokenizer() {
		try (InputStream inputStream = new ClassPathResource("tokenizer.model").getInputStream()) {
			return new SpTokenizer(inputStream.readAllBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}