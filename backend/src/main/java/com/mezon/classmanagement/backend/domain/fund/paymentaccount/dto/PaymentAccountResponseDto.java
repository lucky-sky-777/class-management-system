package com.mezon.classmanagement.backend.domain.fund.paymentaccount.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.common.constant.DateTimeConstant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@JsonPropertyOrder(value = {
		"id",
		"class_id",
		"bank_code",
		"number",
		"name",
		"qr_code_url",
		"creator_user_id",
		"creator_display_name",
		"creator_avatar_url",
		"created_at"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class PaymentAccountResponseDto {

	@JsonProperty(value = "id")
	Long id;

	@JsonProperty(value = "class_id")
	Long classId;

	@JsonProperty(value = "bank_code")
	String bankCode;

	@JsonProperty(value = "number")
	String number;

	@JsonProperty(value = "name")
	String name;

	@JsonProperty(value = "qr_code_url")
	String qrCodeUrl;

	@JsonProperty(value = "creator_user_id")
	Long creatorUserId;

	@JsonProperty(value = "creator_display_name")
	String creatorDisplayName;

	@JsonProperty(value = "creator_avatar_url")
	String creatorAvatarUrl;

	@JsonFormat(pattern = DateTimeConstant.PATTERN_FULL_DATETIME, timezone = DateTimeConstant.TIMEZONE)
	@JsonProperty(value = "created_at")
	Instant createdAt;
	
}