package com.mezon.classmanagement.backend.domain_document.main.directory.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain_document.main.directory.dto.CreateDirectoryRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.directory.entity.Directory;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface DirectoryMapper {

	public Directory toDirectory(CreateDirectoryRequestDto createDirectoryRequestDto);

}