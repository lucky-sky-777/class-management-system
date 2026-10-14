package com.mezon.classmanagement.backend.domain_document.main.document.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public class InternalUpdateDocumentRequestDto {

	Document.Status status;

}