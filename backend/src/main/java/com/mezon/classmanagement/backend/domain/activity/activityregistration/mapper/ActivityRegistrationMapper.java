package com.mezon.classmanagement.backend.domain.activity.activityregistration.mapper;

import com.mezon.classmanagement.backend.domain.activity.activityregistration.dto.response.ActivityRegistrationResponseDto;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.entity.ActivityRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActivityRegistrationMapper {

	@Mapping(source = "clazz.id", target = "classId")
	@Mapping(source = "activity.id", target = "activityId")
	@Mapping(source = "creator.id", target = "creatorUserId")
	@Mapping(source = "actor.id", target = "actorUserId")
	ActivityRegistrationResponseDto toActivityRegistrationResponseDto(ActivityRegistration activityRegistration);

}