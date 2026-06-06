package com.mezon.classmanagement.backend.domain.fund.paymentaccount.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.dto.CreateOrUpdatePaymentAccountRequestDto;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.dto.PaymentAccountResponseDto;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.service.PaymentAccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/classes/{classId}/payment-account")
@RestController
public class PaymentAccountController {

	AuthService authService;
	JwtService jwtService;

	PaymentAccountService paymentAccountService;

	@PreAuthorize("@ClassSecurity.manageFund(#classId)")
	@PutMapping
	public ResponseDTO<PaymentAccountResponseDto> createOrUpdate(
			@PathVariable Long classId,
			@RequestBody CreateOrUpdatePaymentAccountRequestDto request
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		PaymentAccountResponseDto response = paymentAccountService.creatorOrUpdate(
				classId,
				userId,
				request
		);

		return ResponseDTO.<PaymentAccountResponseDto>builder()
				.success(true)
				.message("Create or update payment account successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@GetMapping
	public ResponseDTO<PaymentAccountResponseDto> get(
			@PathVariable Long classId
	) {
		PaymentAccountResponseDto response = paymentAccountService.getByClass(
				classId
		);

		return ResponseDTO.<PaymentAccountResponseDto>builder()
				.success(true)
				.message("Get payment account successful")
				.data(response)
				.build();
	}

}