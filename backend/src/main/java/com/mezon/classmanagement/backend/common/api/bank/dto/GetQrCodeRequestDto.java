package com.mezon.classmanagement.backend.common.api.bank.dto;

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
public final class GetQrCodeRequestDto {

	@JsonProperty(value = "bank_code")
	String bankCode;

	@JsonProperty(value = "account_number")
	String accountNumber;

	@JsonProperty(value = "account_name")
	String accountName;

	@JsonProperty(value = "amount")
	Long amount;

	@JsonProperty(value = "notes")
	String notes;

}