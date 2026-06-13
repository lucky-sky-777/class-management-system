package com.mezon.classmanagement.backend.domain.fund.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.fund.dto.CreateFundRequestDto;
import com.mezon.classmanagement.backend.domain.fund.dto.FundIdResponseDto;
import com.mezon.classmanagement.backend.domain.fund.dto.FundResponseDto;
import com.mezon.classmanagement.backend.domain.fund.dto.FundSummaryResponseDto;
import com.mezon.classmanagement.backend.domain.fund.service.FundService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/classes/{classId}/funds")
@RestController
public class FundController {

	AuthService authService;
	JwtService jwtService;

	FundService fundService;

	@PreAuthorize("@ClassSecurity.manageFund(#classId)")
	@PostMapping
	public ResponseDTO<FundResponseDto> create(
			@PathVariable Long classId,
			@RequestBody CreateFundRequestDto request
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		FundResponseDto response = fundService.create(classId, userId, request);

		return ResponseDTO.<FundResponseDto>builder()
				.success(true)
				.message("Create fund successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassSecurity.manageFund(#classId)")
	@DeleteMapping("/{fundId}")
	public ResponseDTO<FundIdResponseDto> delete(
			@PathVariable Long classId,
			@PathVariable Long fundId
	) {
		FundIdResponseDto response = fundService.delete(classId, fundId);

		return ResponseDTO.<FundIdResponseDto>builder()
				.success(true)
				.message("Delete fund successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@GetMapping
	public ResponseDTO<List<FundResponseDto>> getByClass(
			@PathVariable Long classId
	) {
		List<FundResponseDto> response = fundService.getByClass(classId);

		return ResponseDTO.<List<FundResponseDto>>builder()
				.success(true)
				.message("Get funds successful")
				.data(response)
				.build();
	}

	@PreAuthorize("@ClassSecurity.everyoneInClass(#classId)")
	@GetMapping("/summary")
	public ResponseDTO<FundSummaryResponseDto> getSummary(
			@PathVariable Long classId
	) {
		FundSummaryResponseDto response = fundService.getSummaryByClass(classId);

		return ResponseDTO.<FundSummaryResponseDto>builder()
				.success(true)
				.message("Get fund summary successful")
				.data(response)
				.build();
	}

}