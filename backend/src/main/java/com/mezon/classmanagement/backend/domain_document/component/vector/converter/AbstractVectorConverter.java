package com.mezon.classmanagement.backend.domain_document.component.vector.converter;

import com.mezon.classmanagement.backend.domain_document.component.vector.entity.BaseVector;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public abstract class AbstractVectorConverter<T extends BaseVector> implements AttributeConverter<T, float[]> {

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

		return create(dbData);
	}

	protected abstract T create(float[] values);

}