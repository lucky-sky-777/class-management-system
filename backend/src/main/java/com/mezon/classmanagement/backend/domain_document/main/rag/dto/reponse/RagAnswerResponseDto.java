package com.mezon.classmanagement.backend.domain_document.main.rag.dto.reponse;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RagAnswerResponseDto {
	private String question;
	private String answer;
	private List<DocumentSourceDto> sources;
}