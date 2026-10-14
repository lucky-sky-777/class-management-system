package com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TagResponse {
    Long id;
    String name;
    String slug;
}