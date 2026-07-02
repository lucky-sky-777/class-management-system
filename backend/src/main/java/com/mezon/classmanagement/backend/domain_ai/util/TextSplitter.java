package com.mezon.classmanagement.backend.domain_ai.util;

import java.util.ArrayList;
import java.util.List;

public class TextSplitter {

	public static List<String> splitText(String text, int chunkSize, int chunkOverlap) {
		List<String> chunks = new ArrayList<>();
		if (text == null || text.trim().isEmpty()) {
			return chunks;
		}

		int currentIndex = 0;
		int textLength = text.length();

		while (currentIndex < textLength) {
			int endIndex = Math.min(currentIndex + chunkSize, textLength);

			String chunk = text.substring(currentIndex, endIndex);
			chunks.add(chunk);

			if (endIndex == textLength) {
				break;
			}

			currentIndex += (chunkSize - chunkOverlap);
		}

		return chunks;
	}

}