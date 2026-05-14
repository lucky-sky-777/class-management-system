package com.mezon.classmanagement.backend.domain.fund.fundpayment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.entity.FundPayment;
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
		"fund_id",
		"proof_url",
		"created_at",
		"creator_user_id",
		"status",
		"actor_user_id"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public class FundPaymentResponseDto {

	@JsonProperty(value = "id")
	Long id;

	@JsonProperty(value = "class_id")
	Long classId;

	@JsonProperty(value = "fund_id")
	Long fundId;

	@JsonProperty(value = "proof_url")
	String proofUrl;

	@JsonProperty(value = "created_at")
	Instant createdAt;

	@JsonProperty(value = "creator_user_id")
	Long creatorUserId;

	@JsonProperty(value = "status")
	FundPayment.Status status;

	@JsonProperty(value = "actor_user_id")
	Long actorUserId;

}