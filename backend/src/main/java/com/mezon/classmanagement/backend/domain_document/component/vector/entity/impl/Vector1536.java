package com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl;

import com.mezon.classmanagement.backend.domain_document.component.vector.entity.BaseVector;

public class Vector1536 extends BaseVector {

	public static final int DIMENSION = 1536;

	public Vector1536() {
		super(new float[DIMENSION]);
	}

	public Vector1536(float[] values) {
		super(values);
	}

	@Override
	protected boolean isValidDimension() {
		return getDimension() == DIMENSION;
	}

}