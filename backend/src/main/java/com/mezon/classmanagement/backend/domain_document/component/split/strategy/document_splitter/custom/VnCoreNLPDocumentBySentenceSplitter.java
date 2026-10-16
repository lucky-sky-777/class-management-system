package com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.custom;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByWordSplitter;
import dev.langchain4j.data.document.splitter.HierarchicalDocumentSplitter;
import dev.langchain4j.model.TokenCountEstimator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.pipeline.Annotation;
import vn.pipeline.Sentence;
import vn.pipeline.VnCoreNLP;

public class VnCoreNLPDocumentBySentenceSplitter extends HierarchicalDocumentSplitter {

	private VnCoreNLP vnCoreNLP;

	public VnCoreNLPDocumentBySentenceSplitter(
			int maxSegmentSizeInChars,
			int maxOverlapSizeInChars
	) {
		super(maxSegmentSizeInChars, maxOverlapSizeInChars, null, null);
	}

	public VnCoreNLPDocumentBySentenceSplitter(
			int maxSegmentSizeInChars,
			int maxOverlapSizeInChars,
			DocumentSplitter subSplitter
	) {
		super(maxSegmentSizeInChars, maxOverlapSizeInChars, null, subSplitter);
	}

	public VnCoreNLPDocumentBySentenceSplitter(
			int maxSegmentSizeInTokens,
			int maxOverlapSizeInTokens,
			TokenCountEstimator tokenCountEstimator,
			VnCoreNLP vnCoreNLP
	) {
		super(maxSegmentSizeInTokens, maxOverlapSizeInTokens, tokenCountEstimator, null);
		this.vnCoreNLP = vnCoreNLP;
	}

	public VnCoreNLPDocumentBySentenceSplitter(
			int maxSegmentSizeInTokens,
			int maxOverlapSizeInTokens,
			TokenCountEstimator tokenCountEstimator,
			DocumentSplitter subSplitter
	) {
		super(maxSegmentSizeInTokens, maxOverlapSizeInTokens, tokenCountEstimator, subSplitter);
	}

	@Override
	protected String[] split(String text) {
		Annotation annotation = new Annotation(text);
		try {
			vnCoreNLP.annotate(annotation);
		} catch (Exception e) {
			throw new RuntimeException("Lỗi khi xử lý ngắt câu bằng VnCoreNLP", e);
		}

		String[] sentences = annotation.getSentences().stream()
				.map(Sentence::getRawSentence)
				.toArray(String[]::new);

		if (sentences.length == 0) {
			return new String[] { text };
		}

		return sentences;
	}

	@Override
	protected String joinDelimiter() {
		return " ";
	}

	@Override
	protected DocumentSplitter defaultSubSplitter() {
		return new DocumentByWordSplitter(maxSegmentSize, maxOverlapSize, tokenCountEstimator);
	}

}