package com.mezon.classmanagement.backend.domain_document.main.rag.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentSourceDto {
    Long id;
    String fileName;
}
