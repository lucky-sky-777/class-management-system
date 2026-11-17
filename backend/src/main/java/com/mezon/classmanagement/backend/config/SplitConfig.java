package com.mezon.classmanagement.backend.config;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.googleai.GoogleAiGeminiTokenCountEstimator;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;

import java.time.Duration;

public final class SplitConfig {

	public static final int MAX_SEGMENT_SIZE = 1000;
	public static final int MAX_OVERLAP_SIZE = 100;

	public static final TokenCountEstimator OPEN_AI_TOKEN_COUNT_ESTIMATOR =
			new OpenAiTokenCountEstimator(OpenAiChatModelName.GPT_5_1);

	@SuppressWarnings(value = {WarningConstant.SPELL_CHECKING_INSPECTION})
	@Deprecated
	public static final TokenCountEstimator GEMINI_TOKEN_COUNT_ESTIMATOR =
			new GoogleAiGeminiTokenCountEstimator(
					GoogleAiGeminiTokenCountEstimator.builder()
							.apiKey(System.getenv("GEMINI_API_KEY"))
							.modelName("gemini-3.5-flash")
							.timeout(Duration.ofSeconds(30))
							.maxRetries(2)
			);

}