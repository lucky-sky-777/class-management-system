package com.mezon.classmanagement.backend.domain_document.main.rag.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RagAskRequestDto {

    @NotNull(message = "Document ID không được để trống")
    @JsonProperty("documentId")
    Long documentId;

    @NotBlank(message = "Câu hỏi không được để trống")
    @JsonProperty("question")
    String question;
}
