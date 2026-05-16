package com.mezon.classmanagement.backend.domain.fund.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.domain.fund.entity.Fund;
import jakarta.persistence.Column;
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
		"type",
		"amount",
		"title",
		"description",
		"created_at",
		"creator_user_id"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class FundResponseDto {

	@JsonProperty(value = "id")
	Long id;
	
	@JsonProperty(value = "class_id")
	Long classId;
	
	@JsonProperty(value = "type")
	Fund.Type type;

	@JsonProperty(value = "amount")
	Long amount;

	@JsonProperty(value = "title")
	String title;

	@JsonProperty(value = "description")
	String description;

	@JsonProperty(value = "qr_code_url")
	String qrCodeUrl;

	@JsonProperty(value = "created_at")
	Instant createdAt;
	
	@JsonProperty(value = "creator_user_id")
	Long creatorUserId;
	
}