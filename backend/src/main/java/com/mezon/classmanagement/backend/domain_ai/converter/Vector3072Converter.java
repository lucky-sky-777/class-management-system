package com.mezon.classmanagement.backend.domain_ai.converter;


import com.mezon.classmanagement.backend.domain_ai.entity.Vector3072;

public class Vector3072Converter
		extends AbstractVectorConverter<Vector3072> {

	@Override
	protected Vector3072 createVector(float[] values) {
		return new Vector3072(values);
	}

}