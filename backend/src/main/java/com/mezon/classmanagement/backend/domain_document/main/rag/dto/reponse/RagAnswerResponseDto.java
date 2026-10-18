package com.mezon.classmanagement.backend.domain_document.main.rag.dto.reponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RagAnswerResponseDto {
	private String question;
	private String answer;
}