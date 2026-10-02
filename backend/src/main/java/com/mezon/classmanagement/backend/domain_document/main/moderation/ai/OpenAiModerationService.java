package com.mezon.classmanagement.backend.domain_document.main.moderation.ai;

import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.model.openai.OpenAiModerationModel;
import dev.langchain4j.model.output.Response;
import org.springframework.stereotype.Service;

@Service
public class OpenAiModerationService {

    private final ModerationModel moderationModel;

    public OpenAiModerationService() {
        this.moderationModel = OpenAiModerationModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("omni-moderation-latest")
                .build();
    }

    public boolean isAllowed(String text) {

        if (text == null || text.isBlank()) {
            return false;
        }

        Response<Moderation> response =
                moderationModel.moderate(text);

        return !response.content().flagged();
    }
}
