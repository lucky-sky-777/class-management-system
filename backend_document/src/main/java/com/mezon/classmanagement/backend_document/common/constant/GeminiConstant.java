package com.mezon.classmanagement.backend_document.common.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

public class GeminiConstant {

	public enum TaskType {
		RETRIEVAL_DOCUMENT,
		RETRIEVAL_QUERY
	}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@Getter
	@RequiredArgsConstructor
	public enum Model {
		GEMINI_EMBEDDING_2("models/gemini-embedding-2");

		String name;
	}

}