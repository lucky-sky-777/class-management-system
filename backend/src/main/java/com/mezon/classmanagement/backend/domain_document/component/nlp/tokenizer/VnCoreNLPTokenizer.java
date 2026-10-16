package com.mezon.classmanagement.backend.domain_document.component.nlp.tokenizer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import vn.pipeline.Annotation;
import vn.pipeline.VnCoreNLP;
import vn.pipeline.Word;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class VnCoreNLPTokenizer extends Tokenizer {

	static final int BUFFER_SIZE = 8192;

	static final UrlValidator URL_VALIDATOR = UrlValidator.getInstance();

	final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	final VnCoreNLP vnCoreNLP;

	Iterator<Word> tokenIterator = Collections.emptyIterator();

	public VnCoreNLPTokenizer(VnCoreNLP vnCoreNLP) {
		this.vnCoreNLP = vnCoreNLP;
	}

	/*
	noinspection
	@Override
	public void reset() throws IOException {
		super.reset();

		String text = readInput();
		if (text.isBlank()) {
			tokenIterator = Collections.emptyIterator();
			return;
		}

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
	*/

	@Override
	public void reset() throws IOException {
		super.reset();

		String text = readInput();

		if (text.isBlank()) {
			tokenIterator = Collections.emptyIterator();
			return;
		}

		Annotation annotation = new Annotation(text);

		try {
			vnCoreNLP.annotate(annotation);
			tokenIterator = annotation.getWords().iterator();
		} catch (Exception e) {
			throw new IOException(
					"Failed to process text with VnCoreNLP",
					e
			);
		}
	}

	@Override
	public boolean incrementToken() {
		clearAttributes();

		if (!tokenIterator.hasNext()) {
			return false;
		}

		Word word = tokenIterator.next();

		String form = word.getForm();

		if (!URL_VALIDATOR.isValid(form)) {
			form = form.replace("_", " ");
		}

		termAtt.setEmpty().append(form);

		return true;
	}

	@Override
	public void end() throws IOException {
		super.end();
	}

	@Override
	public void close() throws IOException {
		tokenIterator = Collections.emptyIterator();
		super.close();
	}

	private String readInput() throws IOException {
		StringWriter writer = new StringWriter();

		char[] buffer = new char[BUFFER_SIZE];

		int length;

		while ((length = input.read(buffer)) != -1) {
			writer.write(buffer, 0, length);
		}

		return writer.toString();
	}

}