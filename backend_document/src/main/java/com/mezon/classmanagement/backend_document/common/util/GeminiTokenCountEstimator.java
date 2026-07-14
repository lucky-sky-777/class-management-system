package com.mezon.classmanagement.backend_document.common.util;

import ai.djl.sentencepiece.SpTokenizer;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.TokenCountEstimator;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

public class GeminiTokenCountEstimator implements TokenCountEstimator {

	private static final ClassPathResource classPathResource;
	private static final InputStream inputStream;
	private static final byte[] bytes;

	static {
		classPathResource = new ClassPathResource("tokenizer.model");

		try {
			inputStream = GeminiTokenCountEstimator.classPathResource.getInputStream();
			bytes = inputStream.readAllBytes();
		} catch (IOException ie) {
			throw new RuntimeException(ie.getMessage());
		}
	}

	@Override
	public int estimateTokenCountInText(String text) {
		try (SpTokenizer tokenizer = new SpTokenizer(bytes)) {
			return tokenizer
					.tokenize(text)
					.size();
		}
	}

	@Override
	public int estimateTokenCountInMessage(ChatMessage message) {
		throw new RuntimeException();
	}

	@Override
	public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
		throw new RuntimeException();
	}

}