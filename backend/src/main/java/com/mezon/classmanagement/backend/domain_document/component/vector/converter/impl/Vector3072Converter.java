package com.mezon.classmanagement.backend.domain_document.component.vector.converter.impl;

import com.mezon.classmanagement.backend.domain_document.component.vector.converter.AbstractVectorConverter;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector3072;

public class Vector3072Converter extends AbstractVectorConverter<Vector3072> {

	@Override
	protected Vector3072 create(float[] values) {
		return new Vector3072(values);
	}

}