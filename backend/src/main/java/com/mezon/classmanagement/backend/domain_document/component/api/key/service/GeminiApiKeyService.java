package com.mezon.classmanagement.backend.domain_document.component.api.key.service;

import com.mezon.classmanagement.backend.common.env.GeminiEnv;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain_document.component.api.key.entity.ApiKey;
import com.mezon.classmanagement.backend.domain_document.component.api.key.repository.ApiKeyRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class GeminiApiKeyService {

	GeminiEnv geminiEnv;

	AtomicInteger currentApiKeyIndex = new AtomicInteger(0);

	ApiKeyRepository apiKeyRepository;

	List<String> keyList = new ArrayList<>();
	public List<String> getKeyList() {
		return geminiEnv.apiKeyList;
	}

	Set<String> rateLimitKey = new HashSet<>();

	@Autowired
	public GeminiApiKeyService(ApiKeyRepository apiKeyRepository, GeminiEnv geminiEnv) {
		this.apiKeyRepository = apiKeyRepository;
		this.geminiEnv = geminiEnv;
		//initialize();
	}

	private void initialize() {
		if (keyList.isEmpty()) {
			loadAll();
		}
	}

	public void refresh() {
		keyList.clear();
		loadAll();
	}

	private void loadAll() {
		keyList.addAll(
				apiKeyRepository
						.findAllByProvider(ApiKey.Provider.GEMINI)
						.stream()
						.map(ApiKey::getKeyEncrypt)
						.toList()
		);
	}

	private void status429() {
		System.out.println("Too many request");
		throw new GlobalException(
				GlobalException.Type.TOO_MANY_REQUEST,
				"Too many request"
		);
	}

	public void rateLimit(String key) {
		System.out.println("Rate limit: " + key);
		rateLimitKey.add(key);
		if (rateLimitKey.size() > getKeyList().size() + 1) {
			rateLimitKey.clear();
			status429();
		}
	}

	public String nextApiKey() {
		List<String> apiKeys = getKeyList();

		if (apiKeys == null || apiKeys.isEmpty()) {
			throw new IllegalStateException(
					"GEMINI_API_KEY_LIST không được để trống"
			);
		}

		int index = currentApiKeyIndex.getAndUpdate(
				current -> (current + 1) % apiKeys.size()
		);

		System.out.println(apiKeys.get(index));
		return apiKeys.get(index);
	}

}