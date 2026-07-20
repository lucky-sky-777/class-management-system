package com.mezon.classmanagement.backend_document.domain.component.nlp.stopword;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.WordlistLoader;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StopWordLoader {

	public static Set<String> loadStopWords(String resourcePath) throws IOException {
		try (
				Stream<String> words = new BufferedReader(
						new InputStreamReader(
								new ClassPathResource(resourcePath).getInputStream(),
								StandardCharsets.UTF_8
						)
				).lines()
		) {
			return words
					.map(String::trim)
					.map(String::toLowerCase)
					.filter(word -> !word.isEmpty())
					.filter(word -> !word.startsWith("#"))
					.collect(Collectors.toSet());
		}
	}

	public static CharArraySet loadStopWords2(String resourcePath) throws Exception {
		try (InputStream inputStream = new ClassPathResource(resourcePath).getInputStream()) {
			return WordlistLoader.getWordSet(
					inputStream,
					StandardCharsets.UTF_8,
					"#"
			);
		}
	}

	public static Set<String> loadStopWords3(String resourcePath) throws Exception {
		return new HashSet<>(
				WordlistLoader.getLines(
						new ClassPathResource(resourcePath).getInputStream(),
						StandardCharsets.UTF_8
				));
	}

}