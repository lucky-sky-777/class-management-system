package com.mezon.classmanagement.backend.domain_document.main.document.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.request.CreateDocumentRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface DocumentMapper {

	Document toDocument(CreateDocumentRequestDto createDocumentRequestDto);

}