package com.mezon.classmanagement.backend_document.config;

import com.mezon.classmanagement.backend_document.common.constant.VnCoreNLPConstant;
import com.mezon.classmanagement.backend_document.domain.component.nlp.stopword.StopWordLoader;
import org.apache.lucene.analysis.CharArraySet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.pipeline.VnCoreNLP;

import java.util.Set;

@Configuration
public class VnCoreNLPConfig {

	@Bean
	public VnCoreNLP vnCoreNLP()  {
		try {
			return new VnCoreNLP(VnCoreNLPConstant.ANNOTATORS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public CharArraySet VIETNAMESE_STOP_WORDS() {
		try {
			return StopWordLoader.loadStopWords2("nlp/stopword/github/Tarrasch/vietnamese-stopwords/stopwords.txt");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}