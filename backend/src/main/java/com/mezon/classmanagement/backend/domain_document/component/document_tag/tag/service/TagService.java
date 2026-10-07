package com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.impl.ChunkByParagraphWithoutOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.MultipartFileDocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.nlp.analyzer.VnCoreNLPAnalyzer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class TagService {

	ChunkService chunkService;
	ChunkByParagraphWithoutOverlapStrategy chunkByParagraphWithoutOverlapStrategy;

	VnCoreNLPAnalyzer vnCoreNLPAnalyzer;
	StandardAnalyzer standardAnalyzer = new StandardAnalyzer();

	/**
	 * Code mới viết từ đây
	 */

	@SuppressWarnings({WarningConstant.UNUSED})
	public void placeHolder() {}

	/**
	 * Code mới viết đến đây
	 */

	@Deprecated
	public List<String> extractTagsWithStandard(
			String content,
			int topK
	) throws Exception {
		return getTagList(content, topK, standardAnalyzer);
	}

	@Deprecated
	public List<String> extractTagsWithVnCoreNLP(
			String content,
			int topK
	) throws Exception {
		return getTagList(content, topK, vnCoreNLPAnalyzer);
	}

	public List<String> extractTagsWithVnCoreNLP(
			MultipartFile multipartFile,
			int topK
	) throws Exception {
		DocumentSource documentSource = new MultipartFileDocumentSource(multipartFile);

		List<String> chunkList = chunkService
				.getChunkList(
						chunkByParagraphWithoutOverlapStrategy,
						documentSource
				);

		return getTagListFromChunkList(chunkList, topK, vnCoreNLPAnalyzer);
	}

	public List<String> extractTagsWithVnCoreNLP(
			List<String> chunkList,
			int topK
	) throws Exception {
		return getTagListFromChunkList(chunkList, topK, vnCoreNLPAnalyzer);
	}

	private List<String> getTagList(
			String content,
			int topK,
			Analyzer analyzer
	) throws Exception {
		Map<String, Integer> termFrequencies = new HashMap<>();

		getTermFrequencyMap(content, analyzer, termFrequencies);

		return getTopTagList(termFrequencies, topK);
	}

	private List<String> getTagListFromChunkList(
			List<String> chunkList,
			int topK,
			Analyzer analyzer
	) throws IOException {
		Map<String, Integer> termFrequencies = new HashMap<>();

		for (String chunk : chunkList) {
			if (chunk == null || chunk.isBlank()) {
				continue;
			}

			getTermFrequencyMap(chunk, analyzer, termFrequencies);
		}

		return getTopTagList(termFrequencies, topK);
	}

	private List<String> getTopTagList(
			Map<String, Integer> termFrequencies,
			int topK
	) {
		return termFrequencies.entrySet().stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
				.limit(topK)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	private void getTermFrequencyMap(
			String content,
			Analyzer analyzer,
			Map<String, Integer> termFrequencyMap
	) throws IOException {
		try (TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(content))) {
			CharTermAttribute charTermAttr = tokenStream.addAttribute(CharTermAttribute.class);
			tokenStream.reset();

			while (tokenStream.incrementToken()) {
				String term = charTermAttr.toString().trim();

				if (term.length() > 2 && !term.chars().allMatch(Character::isDigit)) {
					termFrequencyMap.merge(term, 1, Integer::sum);
				}

				/*
				noinspection
				if (term.length() > 2 && !term.matches("\\d+")) {
					termFrequencyMap.put(term, termFrequencyMap.getOrDefault(term, 0) + 1);
				}
				*/
			}

			tokenStream.end();
		}
	}

}