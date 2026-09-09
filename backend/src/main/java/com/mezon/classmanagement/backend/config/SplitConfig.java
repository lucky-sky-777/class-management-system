package com.mezon.classmanagement.backend.config;

import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;

public final class SplitConfig {

	public static final int MAX_SEGMENT_SIZE = 1000;
	public static final int MAX_OVERLAP_SIZE = 100;

	public static final TokenCountEstimator TOKEN_COUNT_ESTIMATOR = new OpenAiTokenCountEstimator(OpenAiChatModelName.GPT_5_1);

}