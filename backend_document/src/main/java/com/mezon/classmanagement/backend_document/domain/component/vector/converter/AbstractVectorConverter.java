package com.mezon.classmanagement.backend_document.domain.component.vector.converter;

import com.mezon.classmanagement.backend_document.domain.component.vector.entity.Vector;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public abstract class AbstractVectorConverter<T extends Vector> implements AttributeConverter<T, float[]> {

	@Override
	public float[] convertToDatabaseColumn(T attribute) {

		if (attribute == null) {
			return null;
		}

		return attribute.getValues();
	}

	@Override
	public T convertToEntityAttribute(float[] dbData) {

		if (dbData == null) {
			return null;
		}

		return createVector(dbData);
	}

	protected abstract T createVector(float[] values);

}