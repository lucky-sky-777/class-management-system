package com.mezon.classmanagement.backend.domain_document.main.interest.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateInterestRequest {
    @NotNull(message = "Danh sách topic không được để null")
    List<Long> topicIds;
}
