package com.mezon.classmanagement.backend.domain_document.component.vector.entity;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public abstract class BaseVector {

	float[] values;
	int dimension;

	public BaseVector(float[] values) {
		this.values = values;
		this.dimension = values.length;

		if (!isValidDimension()) {
			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, "Internal server error");
		}
	}

	protected abstract boolean isValidDimension();

}