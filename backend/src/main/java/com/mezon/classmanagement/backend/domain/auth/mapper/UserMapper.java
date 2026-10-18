package com.mezon.classmanagement.backend.domain.auth.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain.auth.dto.signup.SignUpRequestDto;
import com.mezon.classmanagement.backend.domain.auth.dto.user.UserResponseDto;
import com.mezon.classmanagement.backend.domain.auth.dto.user.request.UpdateUserRequestDto;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.clazz.dto.createandupdate.CreateAndUpdateClassRequestDto;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {
	User toUser(SignUpRequestDto request);
	User toUser(UpdateUserRequestDto updateUserRequestDto);
	UserResponseDto toUserResponseDto(User user);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateUserFromRequestDto(UpdateUserRequestDto request, @MappingTarget User entity);
}