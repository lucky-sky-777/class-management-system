package com.mezon.classmanagement.backend.domain_document.main.interest.topic.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicResponse {
    Long id;
    String name;
    String slug;
}