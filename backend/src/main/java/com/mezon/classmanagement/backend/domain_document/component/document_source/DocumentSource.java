package com.mezon.classmanagement.backend.domain_document.component.document_source;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import dev.langchain4j.data.document.Document;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Set;

public abstract class DocumentSource {

	public static final Set<Class<?>> ALLOWED_SOURCE_TYPES = Set.of(
			String.class,
			MultipartFile.class,
			File.class
	);

	@Getter
	private final Object source;

	public DocumentSource(Object source) {
		if (source == null
				|| ALLOWED_SOURCE_TYPES
				.stream()
				.noneMatch(type -> type.isInstance(source))
		) {
			throw new GlobalException(
					GlobalException.Type.INVALID_REQUEST,
					"Unsupported DocumentSource"
			);
		}

		this.source = source;
	}

	public abstract Document toDocument() throws Exception;

}