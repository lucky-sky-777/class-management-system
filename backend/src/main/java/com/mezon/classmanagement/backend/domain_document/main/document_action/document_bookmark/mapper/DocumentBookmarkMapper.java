package com.mezon.classmanagement.backend.domain_document.main.document_action.document_bookmark.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_bookmark.dto.DocumentBookmarkResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_bookmark.entity.DocumentBookmark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface DocumentBookmarkMapper {

	@Mappings(value = {
			@Mapping(source = "document.id", target = "documentId"),
			@Mapping(source = "creator.id", target = "creatorUserId")
	})
	DocumentBookmarkResponseDto toDocumentBookmarkResponseDto(DocumentBookmark documentBookmark);

	default List<DocumentBookmarkResponseDto> toDocumentBookmarkResponseDtoList(List<DocumentBookmark> documentBookmarkList) {
		return documentBookmarkList.stream()
				.map(this::toDocumentBookmarkResponseDto)
				.toList();
	}

}