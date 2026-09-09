package com.mezon.classmanagement.backend.domain_document.component.nlp.tokenizer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.pipeline.Annotation;
import vn.pipeline.VnCoreNLP;
import vn.pipeline.Word;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class VnCoreNLPTokenizer extends Tokenizer {

	final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	final VnCoreNLP vnCoreNLP;

	Iterator<String> tokenIterator;

	@Autowired
	public VnCoreNLPTokenizer(VnCoreNLP vnCoreNLP) {
		super();
		this.vnCoreNLP = vnCoreNLP;
	}

	@Override
	public void reset() throws IOException {
		super.reset();

		StringWriter writer = new StringWriter();

		char[] buffer = new char[1024];
		int len;
		while ((len = input.read(buffer)) != -1) {
			writer.write(buffer, 0, len);
		}

		String text = writer.toString();
		List<String> tokens = new ArrayList<>();

		if (text != null && !text.trim().isEmpty()) {
			Annotation annotation = new Annotation(text);

			try {
				vnCoreNLP.annotate(annotation);

				for (Word word : annotation.getWords()) {
					String wordStr;

					if (UrlValidator.getInstance().isValid(word.getForm())) {
						wordStr = word.getForm();
					} else {
						wordStr = word.getForm().replace("_", " ");
					}

					tokens.add(wordStr);
				}
			} catch (Exception e) {
				throw new RuntimeException();
			}
		}

		this.tokenIterator = tokens.iterator();
	}

	@Override
	public final boolean incrementToken() {
		clearAttributes();

		if (tokenIterator != null && tokenIterator.hasNext()) {
			String word = tokenIterator.next();
			termAtt.setEmpty().append(word);

			return true;
		}

		return false;
	}

}