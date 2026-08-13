package com.mezon.classmanagement.backend_document.common.constant;

import com.mezon.classmanagement.backend_document.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend_document.common.util.FileUtils;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

//@SuppressWarnings(value = {WarningConstant.UNUSED})
public final class FileConstant {

	public static final class AllowedExtension {
		public static final String DOC = "doc";
		public static final String DOCX = "docx";

		public static final String PPT = "ppt";
		public static final String PPTX = "pptx";

		public static final String PDF = "pdf";

		public static final String TXT = "txt";
	}

	public static final class AllowedMimeType {
		public static final String DOC = FileUtils.getMimeType(AllowedExtension.DOC);
		public static final String DOCX = FileUtils.getMimeType(AllowedExtension.DOCX);

		public static final String PPT = FileUtils.getMimeType(AllowedExtension.PPT);
		public static final String PPTX = FileUtils.getMimeType(AllowedExtension.PPTX);

		public static final String PDF = FileUtils.getMimeType(AllowedExtension.PDF);

		public static final String TXT = FileUtils.getMimeType(AllowedExtension.TXT);
	}

	public static final Set<String> ALLOWED_EXTENSION_SET = getStringConstants(AllowedExtension.class);

	public static final Set<String> ALLOWED_MIME_TYPE_SET = getStringConstants(AllowedMimeType.class);

	private static Set<String> getStringConstants(Class<?> clazz) {
		return Arrays.stream(clazz.getDeclaredFields())
				/* public */.filter(field -> Modifier.isPublic(field.getModifiers()))
				/* static */.filter(field -> Modifier.isStatic(field.getModifiers()))
				/* final  */.filter(field -> Modifier.isFinal(field.getModifiers()))
				/* String */.filter(field -> field.getType().equals(String.class))
				.map(field -> {
					try {
						return (String) field.get(null);
					} catch (IllegalAccessException e) {
						throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR);
					}
				})
				.collect(Collectors.toUnmodifiableSet());
	}

}