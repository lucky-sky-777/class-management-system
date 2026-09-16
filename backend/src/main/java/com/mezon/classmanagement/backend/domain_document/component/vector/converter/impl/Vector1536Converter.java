package com.mezon.classmanagement.backend.domain_document.component.vector.converter.impl;

import com.mezon.classmanagement.backend.domain_document.component.vector.converter.AbstractVectorConverter;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;

public class Vector1536Converter extends AbstractVectorConverter<Vector1536> {

	@Override
	protected Vector1536 create(float[] values) {
		return new Vector1536(values);
	}

}