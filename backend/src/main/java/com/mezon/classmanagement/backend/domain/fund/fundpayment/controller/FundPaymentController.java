package com.mezon.classmanagement.backend.domain.fund.fundpayment.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.CreateFundPaymentRequestDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.FundPaymentResponseDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.service.FundPaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/classes/{classId}/fund-payments")
@RestController
public class FundPaymentController {

	AuthService authService;
	JwtService jwtService;

	FundPaymentService fundPaymentService;

//	@PostMapping("/{fundId}")
//	public ResponseDTO<FundPaymentResponseDto> create(
//			@PathVariable Long classId,
//			@PathVariable Long fundId,
//			@RequestBody CreateFundPaymentRequestDto request
//	) {
//		Authentication authentication = authService.getAuthentication();
//		Long userId = jwtService.extractUserId(authentication);
//
//		FundPaymentResponseDto response = fundPaymentService.create(
//				classId,
//				fundId,
//				userId,
//				request
//		);
//
//
//	}

}