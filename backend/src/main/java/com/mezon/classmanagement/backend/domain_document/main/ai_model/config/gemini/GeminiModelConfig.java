package com.mezon.classmanagement.backend.domain_document.main.ai_model.config.gemini;

import com.mezon.classmanagement.backend.common.env.GeminiEnv;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Configuration
public class GeminiModelConfig {

	GeminiEnv geminiEnv;

	@Bean(name = "geminiChatModel")
	public ChatModel geminiChatModel() {
		return GoogleAiGeminiChatModel.builder()
				.apiKey(geminiEnv.apiKey)
				.modelName(GeminiConfig.Chat.MODEL)
				.temperature(0.0)
				.topP(0.8)
				.build();
	}

	@Bean(name = "geminiEmbeddingModel")
	public EmbeddingModel geminiEmbeddingModel() {
		return GoogleAiEmbeddingModel.builder()
				.apiKey(geminiEnv.apiKey)
				.modelName(GeminiConfig.Embedding.MODEL)
				.outputDimensionality(GeminiConfig.Embedding.DIMENSION)
				.build();
	}

}