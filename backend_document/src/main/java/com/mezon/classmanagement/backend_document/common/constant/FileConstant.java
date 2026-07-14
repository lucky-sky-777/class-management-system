package com.mezon.classmanagement.backend_document.common.constant;

import java.util.Set;

public final class FileConstant {

	public static final Set<String> ALLOWED_EXTENSION_SET = Set.of(
			"doc",
			"docx",

			"ppt",
			"pptx",

			"pdf",

			"txt"
	);

	public static final Set<String> ALLOWED_MIME_TYPE_SET = Set.of(
			"application/msword",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",

			"application/vnd.ms-powerpoint",
			"application/vnd.openxmlformats-officedocument.presentationml.presentation",

			"application/pdf",

			"text/plain"
	);

}