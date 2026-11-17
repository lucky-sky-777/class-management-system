package com.mezon.classmanagement.backend.domain_document.main.ai_model.config.gemini;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;

@SuppressWarnings(value = {WarningConstant.SPELL_CHECKING_INSPECTION})
public final class GeminiConfig {

	public static final class Embedding {

		public static final int DIMENSION = 1536;

		public static final String MODEL = "gemini-embedding-2";

	}

	public static final class Chat {

		public static final String MODEL = "gemini-3.5-flash-lite";

	}

}