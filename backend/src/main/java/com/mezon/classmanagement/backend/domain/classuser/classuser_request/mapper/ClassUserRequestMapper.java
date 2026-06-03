package com.mezon.classmanagement.backend.domain.classuser.classuser_request.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.CreateClassUserRequestRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.response.ClassUserRequestResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.entity.ClassUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface ClassUserRequestMapper {

	ClassUserRequest toClassUserRequest(CreateClassUserRequestRequestDto createClassUserRequestRequestDto);

	@Mapping(source = "clazz.id", target = "classId")
	@Mapping(source = "creator.id", target = "creatorUserId")
	@Mapping(source = "actor.id", target = "actorUserId")
	ClassUserRequestResponseDto toClassUserRequestResponseDto(ClassUserRequest classUserRequest);

}