package com.mezon.classmanagement.backend_document.domain.component.vector.converter.impl;

import com.mezon.classmanagement.backend_document.domain.component.vector.converter.AbstractVectorConverter;
import com.mezon.classmanagement.backend_document.domain.component.vector.entity.impl.Vector3072;

public class Vector3072Converter extends AbstractVectorConverter<Vector3072> {

	@Override
	protected Vector3072 createVector(float[] values) {
		return new Vector3072(values);
	}

}