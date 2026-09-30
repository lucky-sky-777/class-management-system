package com.mezon.classmanagement.backend.domain_document.main.interest.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateInterestRequest {
    @NotEmpty(message = "Lĩnh vực quan tâm không được để trống")
    List<Long> topicIds;
}
