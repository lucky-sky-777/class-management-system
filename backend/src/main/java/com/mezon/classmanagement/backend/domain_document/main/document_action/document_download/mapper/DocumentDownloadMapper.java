package com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.dto.DocumentDownloadResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.entity.DocumentDownload;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.entity.DocumentView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface DocumentDownloadMapper {

	@Mappings(value = {
			@Mapping(source = "document.id", target = "documentId"),
			@Mapping(source = "creator.id", target = "creatorUserId")
	})
	DocumentDownloadResponseDto toDocumentDownloadResponseDto(DocumentDownload documentDownload);

	default List<DocumentDownloadResponseDto> toDocumentDownloadResponseDtoList(List<DocumentDownload> documentDownloadList) {
		return documentDownloadList.stream()
				.map(this::toDocumentDownloadResponseDto)
				.toList();
	}

}