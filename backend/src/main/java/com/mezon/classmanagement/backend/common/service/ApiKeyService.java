package com.mezon.classmanagement.backend.common.service;

import com.mezon.classmanagement.backend.common.env.GeminiEnv;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class ApiKeyService {
	AtomicInteger currentApiKeyIndex = new AtomicInteger(0);

	GeminiEnv geminiEnv;

	List<String> rateLimitKey = new ArrayList<>();

	private void status429() {
		System.out.println("Too many request");
		throw new GlobalException(
				GlobalException.Type.TOO_MANY_REQUEST,
				"Too many request"
		);
	}

	public void rateLimit(String key) {
		List<String> apiKeys = geminiEnv.apiKeyList;
		System.out.println("Rate limit: " + key);
		rateLimitKey.add(key);
		if (rateLimitKey.size() > apiKeys.size()) {
			rateLimitKey.clear();
			status429();
		}
	}

	public String nextApiKey() {
		List<String> apiKeys = geminiEnv.apiKeyList;
		if (apiKeys == null || apiKeys.isEmpty()) {
			throw new IllegalStateException(
					"GEMINI_API_KEY_LIST không được để trống"
			);
		}

		int index = currentApiKeyIndex.getAndUpdate(
				current -> (current + 1) % apiKeys.size()
		);

		return apiKeys.get(index);
	}

}