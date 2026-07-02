package com.mezon.classmanagement.backend.domain_ai.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunkService {

	private static final int CHUNK_SIZE = 800;

	private static final int OVERLAP = 100;

	public List<String> chunk(String text) {

		List<String> chunks = new ArrayList<>();

		int start = 0;

		while (start < text.length()) {

			int end =
					Math.min(start + CHUNK_SIZE,
							text.length());

			String chunk =
					text.substring(start, end);

			chunks.add(chunk);

			start += CHUNK_SIZE - OVERLAP;
		}

		return chunks;
	}
}