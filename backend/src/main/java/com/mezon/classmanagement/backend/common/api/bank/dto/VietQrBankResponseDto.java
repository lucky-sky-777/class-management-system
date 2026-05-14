package com.mezon.classmanagement.backend.common.api.bank.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@JsonPropertyOrder(value = {
		"id",
		"name",
		"code",
		"bin",
		"shortName",
		"logo",
		"transferSupported",
		"lookupSupported",
		"short_name",
		"support",
		"isTransfer",
		"swift_code"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class VietQrBankResponseDto {

	@JsonProperty(value = "id")
	Long id;

	@JsonProperty(value = "name")
	String name;

	@JsonProperty(value = "code")
	String code;

	@JsonProperty(value = "bin")
	String bin;

	@JsonProperty(value = "shortName")
	String shortName;

	@JsonProperty(value = "logo")
	String logo;

	@JsonProperty(value = "transferSupported")
	Integer transferSupported;

	@JsonProperty(value = "lookupSupported")
	Integer lookupSupported;

	@JsonProperty(value = "short_name")
	String shortNameLegacy;

	@JsonProperty(value = "support")
	Integer support;

	@JsonProperty(value = "isTransfer")
	Integer isTransfer;

	@JsonProperty(value = "swift_code")
	String swiftCode;

}