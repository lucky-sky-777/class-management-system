package com.mezon.classmanagement.backend.domain_public.bank.dto.response.vietqr;

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

import java.util.List;

@JsonPropertyOrder(value = {
		"code",
		"desc",
		"data"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class VietQrBankListResponseDto {

	@JsonProperty(value = "code")
	String code;

	@JsonProperty(value = "desc")
	String desc;

	@JsonProperty(value = "data")
	List<VietQrBankResponseDto> data;

}