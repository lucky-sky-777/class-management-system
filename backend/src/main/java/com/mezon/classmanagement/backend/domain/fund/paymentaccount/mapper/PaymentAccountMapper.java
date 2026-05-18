package com.mezon.classmanagement.backend.domain.fund.paymentaccount.mapper;

import com.mezon.classmanagement.backend.domain.fund.paymentaccount.dto.CreateOrUpdatePaymentAccountRequestDto;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.dto.PaymentAccountResponseDto;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.entity.PaymentAccount;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PaymentAccountMapper {
	PaymentAccount toPaymentAccount(CreateOrUpdatePaymentAccountRequestDto createOrUpdatePaymentAccountRequestDto);

	PaymentAccountResponseDto toPaymentAccountResponseDto(PaymentAccount paymentAccount);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updatePaymentAccountFromRequestDto(CreateOrUpdatePaymentAccountRequestDto createOrUpdatePaymentAccountRequestDto, @MappingTarget PaymentAccount paymentAccount);
}