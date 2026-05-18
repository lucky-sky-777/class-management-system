package com.mezon.classmanagement.backend.domain.fund.paymentaccount.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class CreateOrUpdatePaymentAccountRequestDto {

	@JsonProperty(value = "bank_code")
	String bankCode;

	@JsonProperty(value = "number")
	String number;

	@JsonProperty(value = "name")
	String name;

}