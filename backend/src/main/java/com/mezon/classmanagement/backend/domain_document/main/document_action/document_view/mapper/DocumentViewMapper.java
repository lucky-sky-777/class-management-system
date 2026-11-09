package com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_bookmark.entity.DocumentBookmark;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.dto.DocumentViewResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.entity.DocumentView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface DocumentViewMapper {

	@Mappings(value = {
			@Mapping(source = "document.id", target = "documentId"),
			@Mapping(source = "creator.id", target = "creatorUserId")
	})
	DocumentViewResponseDto toDocumentViewResponseDto(DocumentView documentView);

	default List<DocumentViewResponseDto> toDocumentViewResponseDtoList(List<DocumentView> documentViewList) {
		return documentViewList.stream()
				.map(this::toDocumentViewResponseDto)
				.toList();
	}

}