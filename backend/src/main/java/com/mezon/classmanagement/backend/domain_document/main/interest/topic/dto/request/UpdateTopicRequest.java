package com.mezon.classmanagement.backend.domain_document.main.interest.topic.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateTopicRequest {
    @NotBlank(message = "Tên topic không được để trống")
    String name;
    @NotBlank(message = "Slug không được để trống")
    String slug;
}
