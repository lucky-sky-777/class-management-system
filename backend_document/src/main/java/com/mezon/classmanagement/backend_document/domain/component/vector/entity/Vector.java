package com.mezon.classmanagement.backend_document.domain.component.vector.entity;

import lombok.Getter;

import java.util.Arrays;

public abstract class Vector {

	protected final float[] values;

	@Getter
	protected final short dimension;

	protected Vector(float[] values, short dimension) {
		if (values.length != dimension) {
			throw new IllegalArgumentException(
					"Expected dimension " + dimension
							+ " but got " + values.length
			);
		}

		this.values = Arrays.copyOf(values, values.length);
		this.dimension = dimension;
	}

	public float[] getValues() {
		return Arrays.copyOf(values, values.length);
	}

	@Override
	public String toString() {
		return Arrays.toString(values);
	}

}