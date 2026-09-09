package com.mezon.classmanagement.backend.common.util;

import ai.djl.sentencepiece.SpTokenizer;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.TokenCountEstimator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class GeminiTokenCountEstimator implements TokenCountEstimator {

	SpTokenizer spTokenizer;

	@Override
	public int estimateTokenCountInText(String text) {
		return spTokenizer.tokenize(text).size();
	}

	@Override
	public int estimateTokenCountInMessage(ChatMessage message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
		throw new UnsupportedOperationException();
	}

}