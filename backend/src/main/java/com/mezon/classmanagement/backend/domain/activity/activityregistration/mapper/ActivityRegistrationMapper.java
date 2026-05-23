package com.mezon.classmanagement.backend.domain.activity.activityregistration.mapper;

import com.mezon.classmanagement.backend.domain.activity.activityregistration.dto.request.UpdateActivityRegistrationRequestDto;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.dto.response.ActivityRegistrationResponseDto;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.entity.ActivityRegistration;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActivityRegistrationMapper {

	@Mapping(source = "clazz.id", target = "classId")
	@Mapping(source = "activity.id", target = "activityId")
	@Mapping(source = "creator.id", target = "creatorUserId")
	@Mapping(source = "actor.id", target = "actorUserId")
	ActivityRegistrationResponseDto toActivityRegistrationResponseDto(ActivityRegistration activityRegistration);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateActivityRegistrationFromRequestDto(UpdateActivityRegistrationRequestDto updateActivityRegistrationRequestDto, @MappingTarget ActivityRegistration activityRegistration);

}