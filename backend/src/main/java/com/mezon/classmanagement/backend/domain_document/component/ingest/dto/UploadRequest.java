package com.mezon.classmanagement.backend.domain_document.component.ingest.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadRequest {
    List<String> tagNames;
}
