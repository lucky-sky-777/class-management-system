package com.mezon.classmanagement.backend.domain_document.main.ai_model.service.chat;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.main.rag.assistant.RagAssistant;
import com.mezon.classmanagement.backend.domain_document.main.rag.constant.RagConstant;
import dev.langchain4j.exception.HttpException;
import dev.langchain4j.exception.LangChain4jException;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.service.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@SuppressWarnings(value = {WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class GeminiChatService {

	RagAssistant ragAssistant;

	public void chat(String message) {
		Result<String> result = ragAssistant.chat(message);
	}

	public String chatRag(String message) {
		try {
			Result<String> result = ragAssistant.chat(message);

			if (result.finishReason() == FinishReason.CONTENT_FILTER) {
				System.out.println("[CONTENT_FILTER]");
				return RagConstant.DOCUMENT_NOT_MENTION;
			}

			String answer = result.content();
			if (answer != null && !answer.isBlank()) {
				return answer.trim();
			}

			System.out.println("[NO_RESPONSE]");
			return RagConstant.DOCUMENT_NOT_MENTION;
		} catch (LangChain4jException e) {
			if (e.getCause() instanceof HttpException httpEx) {
				throw new RuntimeException(
						"Lỗi gọi Gemini API (HTTP " + httpEx.statusCode() + "): " + httpEx.getMessage(), e);
			}
			throw new RuntimeException("Lỗi gọi Gemini API: " + e.getMessage(), e);
		}
	}

}