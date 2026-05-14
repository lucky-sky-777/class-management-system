package com.mezon.classmanagement.backend.domain.fund.mapper;

import com.mezon.classmanagement.backend.domain.fund.dto.CreateFundRequestDto;
import com.mezon.classmanagement.backend.domain.fund.dto.FundResponseDto;
import com.mezon.classmanagement.backend.domain.fund.entity.Fund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FundMapper {
	Fund toFund(CreateFundRequestDto createFundRequestDto);

	@Mapping(source = "clazz.id", target = "classId")
	@Mapping(source = "creator.id", target = "creatorUserId")
	FundResponseDto toFundResponseDto(Fund fund);
}