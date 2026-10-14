package com.mezon.classmanagement.backend.common.util;

import java.util.regex.Pattern;

public class TextUtils {

	private static final Pattern NEWLINE_PATTERN = Pattern.compile("\\n+");

	public static String clean(String rawText) {
		if (rawText == null || rawText.isEmpty()) {
			return rawText;
		}

		// Chuẩn hóa xuống dòng Windows và kiểu cũ thành \n
		String normalizedText = rawText
				.replace("\r\n", "\n")
				.replace('\r', '\n');

		// Xuống dòng đơn thành dấu cách; từ 2 lần trở lên giữ đúng 2 lần
		return NEWLINE_PATTERN.matcher(normalizedText)
				.replaceAll(match ->
						match.group().length() == 1 ? " " : "\n\n"
				);
	}

}

/*

String cleanedText = rawText
        // Chuẩn hóa xuống dòng Windows (\r\n) và kiểu cũ (\r) thành \n
        .replace("\r\n", "\n")
        .replace('\r', '\n')

        // Thay 3 hoặc nhiều ký tự xuống dòng liên tiếp thành 2
        .replaceAll("\\n{3,}", "\n\n")

        // Thay xuống dòng đơn bằng dấu cách, giữ nguyên dòng trống
        .replaceAll("(?<!\\n)\\n(?!\\n)", " ");

*/