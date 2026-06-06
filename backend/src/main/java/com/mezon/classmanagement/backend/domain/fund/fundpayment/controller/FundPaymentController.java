package com.mezon.classmanagement.backend.domain.fund.fundpayment.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.CreateFundPaymentRequestDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.FundPaymentIdResponseDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.FundPaymentResponseDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.service.FundPaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/classes/{classId}/funds/{fundId}/fund-payments")
@RestController
public class FundPaymentController {

	AuthService authService;
	JwtService jwtService;

	FundPaymentService fundPaymentService;

	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@PostMapping
	public ResponseDTO<FundPaymentResponseDto> create(
			@PathVariable Long classId,
			@PathVariable Long fundId,
			@RequestBody CreateFundPaymentRequestDto request
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		FundPaymentResponseDto response = fundPaymentService.create(
				classId,
				fundId,
				userId,
				request
		);

		return ResponseDTO.<FundPaymentResponseDto>builder()
				.success(true)
				.message("Create fund payment successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassSecurity.manageFund(#classId)")
	@PatchMapping("/{fundPaymentId}/approve")
	public ResponseDTO<FundPaymentIdResponseDto> approve(
			@PathVariable Long classId,
			@PathVariable Long fundId,
			@PathVariable Long fundPaymentId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		FundPaymentIdResponseDto response = fundPaymentService.approve(classId, fundId, userId, fundPaymentId);

		return ResponseDTO.<FundPaymentIdResponseDto>builder()
				.success(true)
				.message("Approve fund payment successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassSecurity.manageFund(#classId)")
	@PatchMapping("/{fundPaymentId}/reject")
	public ResponseDTO<FundPaymentIdResponseDto> reject(
			@PathVariable Long classId,
			@PathVariable Long fundId,
			@PathVariable Long fundPaymentId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		FundPaymentIdResponseDto response = fundPaymentService.reject(classId, fundId, userId, fundPaymentId);

		return ResponseDTO.<FundPaymentIdResponseDto>builder()
				.success(true)
				.message("Reject fund payment successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@PatchMapping("/{fundPaymentId}/cancel")
	public ResponseDTO<FundPaymentIdResponseDto> cancel(
			@PathVariable Long classId,
			@PathVariable Long fundId,
			@PathVariable Long fundPaymentId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		FundPaymentIdResponseDto response = fundPaymentService.cancel(classId, fundId, userId, fundPaymentId);

		return ResponseDTO.<FundPaymentIdResponseDto>builder()
				.success(true)
				.message("Cancel fund payment successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@GetMapping
	public ResponseDTO<List<FundPaymentResponseDto>> getByClassAndFund(
			@PathVariable Long classId,
			@PathVariable Long fundId
	) {
		List<FundPaymentResponseDto> response = fundPaymentService.getByClassAndFund(classId, fundId);

		return ResponseDTO.<List<FundPaymentResponseDto>>builder()
				.success(true)
				.message("Get fund payments successful")
				.data(response)
				.build();
	}

}