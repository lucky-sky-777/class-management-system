package com.mezon.classmanagement.backend.domain_document.component.nlp.analyzer;

import com.mezon.classmanagement.backend.domain_document.component.nlp.filter.UrlFilter;
import com.mezon.classmanagement.backend.domain_document.component.nlp.tokenizer.VnCoreNLPTokenizer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.springframework.stereotype.Component;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class VnCoreNLPAnalyzer extends Analyzer {

	VnCoreNLPTokenizer vnCoreNLPTokenizer;

	CharArraySet VIETNAMESE_STOP_WORDS;

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		TokenStream filter = new LowerCaseFilter(vnCoreNLPTokenizer);

		filter = new UrlFilter(filter);
		filter = new StopFilter(filter, VIETNAMESE_STOP_WORDS);

		return new TokenStreamComponents(vnCoreNLPTokenizer, filter);
	}

}