package com.mezon.classmanagement.backend.domain.absencerequest.mapper;

import com.mezon.classmanagement.backend.domain.absencerequest.dto.request.CreateAbsenceRequestRequestDto;
import com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestResponseDto;
import com.mezon.classmanagement.backend.domain.absencerequest.entity.AbsenceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AbsenceRequestMapper {

    AbsenceRequest toAbsenceRequest(CreateAbsenceRequestRequestDto createAbsenceRequestRequestDto);

    @Mapping(source = "clazz.id", target = "classId")
    @Mapping(source = "user.id", target = "userId")
    AbsenceRequestResponseDto toAbsenceRequestResponseDto(AbsenceRequest absenceRequest);

}