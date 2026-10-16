package com.mezon.classmanagement.backend.domain_document.main.directory.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain_document.main.directory.dto.CreateDirectoryRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.directory.dto.DirectoryResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.directory.entity.Directory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = MapStructConfig.class)
public interface DirectoryMapper {

	Directory toDirectory(CreateDirectoryRequestDto createDirectoryRequestDto);

	@Mappings(value = {
			@Mapping(source = "clazz.id", target = "classId"),
			@Mapping(source = "creator.id", target = "creatorUserId")
	})
	DirectoryResponseDto toDirectoryResponseDto(Directory directory);

}