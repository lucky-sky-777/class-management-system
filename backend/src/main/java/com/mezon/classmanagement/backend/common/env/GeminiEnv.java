package com.mezon.classmanagement.backend.common.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeminiEnv {

	@Value("${gemini.api-key}")
	public String apiKey;

	@Value("${gemini.api-key-list}")
	public List<String> apiKeyList;

	@Value("${gemini.embedding-url}")
	public String apiUrl;

	@Value("${gemini.batch-embedding-url}")
	public String batchApiUrl;

}