package com.mezon.classmanagement.backend.domain_document.main.interest.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InterestResponse {
    Long id;
    Long userId;
    List<Long> topicIds;
}
