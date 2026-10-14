package com.mezon.classmanagement.backend.common.constant;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class FileTypeUtils {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Order {
		int value();
	}

	public static final class AllowedExtension {
		@Order(1)
		public static final String DOC = "doc";
		@Order(2)
		public static final String DOCX = "docx";

		@Order(3)
		public static final String PPT = "ppt";
		@Order(4)
		public static final String PPTX = "pptx";

		@Order(5)
		public static final String PDF = "pdf";

		@Order(6)
		public static final String TXT = "txt";
	}

	public static final class AllowedMimeType {
		@Order(1)
		public static final String DOC = FileUtils.detectMimeType(AllowedExtension.DOC);
		@Order(2)
		public static final String DOCX = FileUtils.detectMimeType(AllowedExtension.DOCX);

		@Order(3)
		public static final String PPT = FileUtils.detectMimeType(AllowedExtension.PPT);
		@Order(4)
		public static final String PPTX = FileUtils.detectMimeType(AllowedExtension.PPTX);

		@Order(5)
		public static final String PDF = FileUtils.detectMimeType(AllowedExtension.PDF);

		@Order(6)
		public static final String TXT = FileUtils.detectMimeType(AllowedExtension.TXT);
	}

	public static final Set<String> ALLOWED_EXTENSION_SET
			= getStringConstants(AllowedExtension.class);

	public static final Set<String> ALLOWED_MIME_TYPE_SET
			= getStringConstants(AllowedMimeType.class);

	public static BiMap<String, String> biMap;

	static {
		biMap = createBiMap(
				AllowedExtension.class,
				AllowedMimeType.class
		);
	}

	private static BiMap<String, String> createBiMap(
			@SuppressWarnings({"all"})
			Class<?> keyClass,
			@SuppressWarnings({"all"})
			Class<?> valueClass
	) {
		Set<String> keys = getStringConstants(keyClass);
		Set<String> values = getStringConstants(valueClass);

		if (keys.size() != values.size()) {
			throw new IllegalArgumentException(
					"Key and value constants must have the same size"
			);
		}

		BiMap<String, String> map = HashBiMap.create();

		Iterator<String> keyIterator = keys.iterator();
		Iterator<String> valueIterator = values.iterator();

		while (keyIterator.hasNext()) {
			map.put(keyIterator.next(), valueIterator.next());
		}

		return map;
	}

	@SuppressWarnings({WarningConstant.BOOLEAN_METHOD_IS_ALWAYS_INVERTED})
	public static boolean isValidExtension(String extension) {
		return ALLOWED_EXTENSION_SET.contains(extension.toLowerCase(Locale.ROOT));
	}

	@SuppressWarnings({WarningConstant.BOOLEAN_METHOD_IS_ALWAYS_INVERTED})
	public static boolean isValidMimeType(String mimeType) {
		return ALLOWED_MIME_TYPE_SET.contains(mimeType.toLowerCase(Locale.ROOT));
	}

	public static void invalidExtension() {
		throw new GlobalException(
				GlobalException.Type.INVALID_REQUEST,
				"Unsupported extension. Supported extensions: "
						+ String.join(", ", FileTypeUtils.ALLOWED_EXTENSION_SET)
		);
	}

	public static void invalidMimeType() {
		throw new GlobalException(
				GlobalException.Type.INVALID_REQUEST,
				"Unsupported mime type. Supported mime types: "
						+ String.join(", ", FileTypeUtils.ALLOWED_MIME_TYPE_SET)
		);
	}

	public static String getExtension(String mimeType) {
		if (!isValidMimeType(mimeType)) {
			invalidMimeType();
		}

		return biMap.inverse().get(mimeType);
	}

	public static String getMimeType(String extension) {
		if (!isValidExtension(extension)) {
			invalidExtension();
		}

		return biMap.get(extension);
	}

	public static Set<String> getStringConstants(Class<?> clazz) {
		return Arrays.stream(clazz.getDeclaredFields())
				/* public */.filter(field -> Modifier.isPublic(field.getModifiers()))
				/* static */.filter(field -> Modifier.isStatic(field.getModifiers()))
				/* final  */.filter(field -> Modifier.isFinal(field.getModifiers()))
				/* String */.filter(field -> field.getType().equals(String.class))
				/* Order */.filter(field -> field.isAnnotationPresent(Order.class))
				.sorted(Comparator.comparingInt(
						field -> field.getAnnotation(Order.class).value()
				))
				.map(field -> {
					try {
						return (String) field.get(null);
					} catch (IllegalAccessException e) {
						throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR);
					}
				})
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

}