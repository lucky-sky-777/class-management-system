package com.mezon.classmanagement.backend.domain.deprecated.fund.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain.deprecated.fund.dto.CreateFundRequestDto;
import com.mezon.classmanagement.backend.domain.deprecated.fund.dto.FundResponseDto;
import com.mezon.classmanagement.backend.domain.deprecated.fund.entity.Fund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface FundMapper {
	Fund toFund(CreateFundRequestDto createFundRequestDto);

	@Mapping(source = "clazz.id", target = "classId")
	@Mapping(source = "creator.id", target = "creatorUserId")
	FundResponseDto toFundResponseDto(Fund fund);
}