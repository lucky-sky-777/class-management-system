package com.mezon.classmanagement.backend.domain.deprecated.absencerequest.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain.deprecated.absencerequest.dto.request.CreateAbsenceRequestRequestDto;
import com.mezon.classmanagement.backend.domain.deprecated.absencerequest.dto.response.AbsenceRequestResponseDto;
import com.mezon.classmanagement.backend.domain.deprecated.absencerequest.entity.AbsenceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface AbsenceRequestMapper {

    AbsenceRequest toAbsenceRequest(CreateAbsenceRequestRequestDto createAbsenceRequestRequestDto);

    @Mapping(source = "clazz.id", target = "classId")
    @Mapping(source = "creator.id", target = "creatorUserId")
    @Mapping(source = "actor.id", target = "actorUserId")
    AbsenceRequestResponseDto toAbsenceRequestResponseDto(AbsenceRequest absenceRequest);

}