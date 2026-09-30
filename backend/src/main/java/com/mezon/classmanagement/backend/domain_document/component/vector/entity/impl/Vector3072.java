package com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl;

import com.mezon.classmanagement.backend.domain_document.component.vector.entity.BaseVector;

public class Vector3072 extends BaseVector {

	public static final int DIMENSION = 3072;

	public Vector3072() {
		super(new float[DIMENSION]);
	}

	public Vector3072(float[] values) {
		super(values);
	}

	@Override
	protected boolean isValidDimension() {
		return getDimension() == DIMENSION;
	}

}