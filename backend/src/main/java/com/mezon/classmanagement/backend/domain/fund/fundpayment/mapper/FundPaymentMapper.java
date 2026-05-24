package com.mezon.classmanagement.backend.domain.fund.fundpayment.mapper;

import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.CreateFundPaymentRequestDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.FundPaymentResponseDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.entity.FundPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FundPaymentMapper {
	FundPayment toFundPayment(CreateFundPaymentRequestDto createFundPaymentRequestDto);

	@Mapping(source = "clazz.id", target = "classId")
	@Mapping(source = "fund.id", target = "fundId")
	@Mapping(source = "creator.id", target = "creatorUserId")
	@Mapping(source = "actor.id", target = "actorUserId")
	FundPaymentResponseDto toFundPaymentResponseDto(FundPayment fundPayment);
}