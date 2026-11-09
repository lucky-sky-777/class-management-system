package com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.dto;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TagCreateRequest {
    String name;
}
