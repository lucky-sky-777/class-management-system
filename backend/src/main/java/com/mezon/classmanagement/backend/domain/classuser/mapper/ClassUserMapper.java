package com.mezon.classmanagement.backend.domain.classuser.mapper;

import com.mezon.classmanagement.backend.domain.classuser.dto.ClassUserResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.dto.UpdateClassUserPermissionsRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.dto.UpdateClassUserRoleAndPermissionRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.dto.UpdateClassUserRoleRequestDto;
import com.mezon.classmanagement.backend.domain.groupuser.dto.request.UpdateGroupUserSeatRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.entity.ClassUser;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClassUserMapper {
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateClassUserFromRoleRequestDto(UpdateClassUserRoleRequestDto updateClassUserRoleRequestDto, @MappingTarget ClassUser classUser);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateClassUserFromPermissionsRequestDto(UpdateClassUserPermissionsRequestDto updateClassUserPermissionsRequestDto, @MappingTarget ClassUser classUser);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateClassUserFromRoleAndPermissionRequestDto(UpdateClassUserRoleAndPermissionRequestDto updateClassUserRoleAndPermissionRequestDto, @MappingTarget ClassUser classUser);

	@Mapping(source = "clazz.id", target = "classId")
	@Mapping(source = "user.id", target = "userId")
	ClassUserResponseDto toClassUserResponseDto(ClassUser classUser);
}