package com.mezon.classmanagement.backend.domain_document.main.rag.config;

import com.mezon.classmanagement.backend.common.env.GeminiEnv;
import com.mezon.classmanagement.backend.domain_document.main.rag.assistant.RagAssistant;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Configuration
public class RagConfig {

	@Bean
	public RagAssistant ragAssistant(
			ChatModel chatModel
	) {
		return AiServices.builder(RagAssistant.class)
				.chatModel(chatModel)
				.build();
	}

}