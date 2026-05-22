package com.mezon.classmanagement.backend.domain_public.bank.dto.request;

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
public final class GetBankQrCodeRequestDto {

	@JsonProperty(value = "bank_code")
	String bankCode;

	@JsonProperty(value = "bank_account_number")
	String bankAccountNumber;

	@JsonProperty(value = "bank_account_name")
	String bankAccountName;

	@JsonProperty(value = "transfer_amount")
	Long transferAmount;

	@JsonProperty(value = "transfer_note")
	String transferNote;

}