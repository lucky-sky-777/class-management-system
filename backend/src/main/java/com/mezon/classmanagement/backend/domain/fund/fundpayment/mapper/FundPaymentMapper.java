package com.mezon.classmanagement.backend.domain.fund.fundpayment.mapper;

import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.CreateFundPaymentRequestDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.FundPaymentResponseDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.entity.FundPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FundPaymentMapper {
	FundPayment toFundPayment(CreateFundPaymentRequestDto createFundPaymentRequestDto);

	@Mapping(source = "clazz.id", target = "classId")
	@Mapping(source = "fund.id", target = "fundId")
	@Mapping(source = "creator.id", target = "creatorUserId")
	FundPaymentResponseDto toFundPaymentResponseDto(FundPayment fundPayment);
}