package com.mezon.classmanagement.backend.domain_document.main.rag.assistant;

import com.mezon.classmanagement.backend.domain_document.main.rag.constant.RagConstant;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;

public interface RagAssistant {

	@SystemMessage(RagConstant.SYSTEM_INSTRUCTION)
	Result<String> chat(String userMessage);

}