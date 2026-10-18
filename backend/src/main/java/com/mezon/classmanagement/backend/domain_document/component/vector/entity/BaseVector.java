package com.mezon.classmanagement.backend.domain_document.component.vector.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class BaseVector {

	float[] values;
	int dimension;

	public BaseVector(float[] values) {
		this.values = values;
		this.dimension = values.length;
	}


	@Override
	public String toString() {
		return Arrays.toString(values);
	}

}