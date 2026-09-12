package com.mezon.classmanagement.backend.common.util;

public final class GeminiPromptBuilder {

	public static String buildQueryPrompt(String content) {
		return String.format("task: search result | query: %s", content);
	}

	public static String buildDocumentPrompt(
			String title,
			String content
	) {
		if (title == null || title.isEmpty()) {
			title = "none";
		}

		return String.format("title: %s | text: %s", title, content);
	}

}