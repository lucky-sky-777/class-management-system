package com.mezon.classmanagement.backend_document.domain.component.tag.service;

import com.mezon.classmanagement.backend_document.domain.component.nlp.analyzer.VnCoreNLPAnalyzer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;
import vn.pipeline.VnCoreNLP;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class TagService {

	VnCoreNLPAnalyzer vnCoreNLPAnalyzer;
	StandardAnalyzer standardAnalyzer = new StandardAnalyzer();

	public List<String> extractTagsWithStandard(
			String content,
			int topK
	) throws Exception {
		return extractTags(content, topK, standardAnalyzer);
	}

	public List<String> extractTagsWithVnCoreNLP(
			String content,
			int topK
	) throws Exception {
		return extractTags(content, topK, vnCoreNLPAnalyzer);
	}

	public List<String> extractTags(
			String content,
			int topK,
			Analyzer analyzer
	) throws Exception {
		Map<String, Integer> termFrequencies = new HashMap<>();

		try (TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(content))) {
			CharTermAttribute charTermAttr = tokenStream.addAttribute(CharTermAttribute.class);
			tokenStream.reset();

			while (tokenStream.incrementToken()) {
				String term = charTermAttr.toString().trim();

				if (term.length() > 2 && !term.matches("\\d+")) {
					termFrequencies.put(term, termFrequencies.getOrDefault(term, 0) + 1);
				}
			}

			tokenStream.end();
		}

		return termFrequencies.entrySet().stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
				.limit(topK)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

}