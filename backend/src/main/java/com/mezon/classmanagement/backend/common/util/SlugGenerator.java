package com.mezon.classmanagement.backend.common.util;

import java.text.Normalizer;
import java.util.Locale;

public final class SlugGenerator {

	private SlugGenerator() {
	}

	// Trí Tuệ Nhân Tạo => tri-tue-nhan-tao
	public static String generate(String source) {
		if (source == null || source.isBlank()) {
			return "";
		}

		String slug = source
				.toLowerCase(Locale.ROOT)
				.replace('đ', 'd');

		slug = Normalizer.normalize(slug, Normalizer.Form.NFD)
				.replaceAll("\\p{M}+", "");

		/*

		slug = Normalizer
				.normalize(
						source
								.toLowerCase(Locale.ROOT)
								.replace('đ', 'd'),
						Normalizer.Form.NFD
				)
				.replaceAll("\\p{M}+", "")
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("^-+|-+$", "");

		*/

		return slug
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("^-+|-+$", "");
	}

}