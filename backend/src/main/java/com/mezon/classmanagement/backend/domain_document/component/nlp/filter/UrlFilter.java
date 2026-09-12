package com.mezon.classmanagement.backend.domain_document.component.nlp.filter;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.lucene.analysis.FilteringTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class UrlFilter extends FilteringTokenFilter {

	private static final UrlValidator URL_VALIDATOR = UrlValidator.getInstance();

	private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);

	public UrlFilter(TokenStream in) {
		super(in);
	}

	@Override
	protected boolean accept() {
		return !URL_VALIDATOR.isValid(termAttr.toString());
	}

}