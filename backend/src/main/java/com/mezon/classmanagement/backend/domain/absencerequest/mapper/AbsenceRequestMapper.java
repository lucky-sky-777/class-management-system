package com.mezon.classmanagement.backend.domain.absencerequest.mapper;

import com.mezon.classmanagement.backend.domain.absencerequest.dto.request.CreateAbsenceRequestRequestDto;
import com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestResponseDto;
import com.mezon.classmanagement.backend.domain.absencerequest.entity.AbsenceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AbsenceRequestMapper {

    AbsenceRequest toAbsenceRequest(CreateAbsenceRequestRequestDto createAbsenceRequestRequestDto);

    @Mapping(source = "clazz.id", target = "classId")
    @Mapping(source = "creator.id", target = "creatorUserId")
    @Mapping(source = "actor.id", target = "actorUserId")
    AbsenceRequestResponseDto toAbsenceRequestResponseDto(AbsenceRequest absenceRequest);

}