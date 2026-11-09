package com.mezon.classmanagement.backend.domain_document.main.document.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.request.CreateDocumentRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.request.InternalUpdateDocumentRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface DocumentMapper {

	Document toDocument(CreateDocumentRequestDto createDocumentRequestDto);

	@Mapping(source = "creator.id", target = "creatorUserId")
	DocumentResponseDto toDocumentResponseDto(Document document);

	default List<DocumentResponseDto> toDocumentResponseDtoList(List<Document> documentList) {
		return documentList.stream()
				.map(this::toDocumentResponseDto)
				.toList();
	}

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateDocumentFromRequestDto(InternalUpdateDocumentRequestDto internalUpdateDocumentRequestDto, @MappingTarget Document document);

}