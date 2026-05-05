package com.mezon.classmanagement.backend.domain.classuser.classuser_request.mapper;

import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.CreateClassUserRequestRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.response.ClassUserRequestResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.entity.ClassUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClassUserRequestMapper {

	ClassUserRequest toClassUserRequest(CreateClassUserRequestRequestDto createClassUserRequestRequestDto);

	@Mapping(source = "clazz.id", target = "classId")
	@Mapping(source = "user.id", target = "userId")
	ClassUserRequestResponseDto toClassUserRequestResponseDto(ClassUserRequest classUserRequest);

}