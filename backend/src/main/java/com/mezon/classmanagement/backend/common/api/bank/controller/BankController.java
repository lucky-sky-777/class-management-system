package com.mezon.classmanagement.backend.common.api.bank.controller;

import com.mezon.classmanagement.backend.common.api.bank.dto.BankQrCodeUrlResponseDto;
import com.mezon.classmanagement.backend.common.api.bank.dto.GetQrCodeRequestDto;
import com.mezon.classmanagement.backend.common.api.bank.dto.VietQrBankResponseDto;
import com.mezon.classmanagement.backend.common.api.bank.service.BankService;
import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/public/banks")
@RestController
public class BankController {

	BankService bankService;
	RestTemplate restTemplate;

	@GetMapping
	public ResponseDTO<List<VietQrBankResponseDto>> getBanks() {
		List<VietQrBankResponseDto> response = bankService.getBanks();

		return ResponseDTO.<List<VietQrBankResponseDto>>builder()
				.success(true)
				.message("Get banks successful")
				.data(response)
				.build();
	}

	@GetMapping("/qrcode-url")
	public ResponseDTO<BankQrCodeUrlResponseDto> getQrCodeUrl(
			@RequestBody GetQrCodeRequestDto request
	) {
		BankQrCodeUrlResponseDto response = bankService.getQrCodeUrl(request);

		return ResponseDTO.<BankQrCodeUrlResponseDto>builder()
				.success(true)
				.message("Get QR code successful")
				.data(response)
				.build();
	}

	@Deprecated
	@GetMapping(value = "/qrcode-image", produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<byte[]> getQrCodeImage(
			@RequestParam(required = true) String bankCode,
			@RequestParam(required = true) String accountNumber,
			@RequestParam(required = false) String accountName,
			@RequestParam(required = false) Long amount,
			@RequestParam(required = false) String notes
	) {
		GetQrCodeRequestDto request = GetQrCodeRequestDto.builder()
				.bankCode(bankCode)
				.accountNumber(accountNumber)
				.accountName(accountName)
				.amount(amount)
				.notes(notes)
				.build();

		BankQrCodeUrlResponseDto bankQrCodeUrlResponseDto = bankService.getQrCodeUrl(request);

		String url = bankQrCodeUrlResponseDto.getQrCodeUrl();

		ResponseEntity<byte[]> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				null,
				byte[].class
		);

		return ResponseEntity.ok()
				.contentType(MediaType.IMAGE_PNG)
				.body(response.getBody());
	}

	@GetMapping(value = "/qrcode-image2", produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<byte[]> getQrCodeImage(
			@RequestBody GetQrCodeRequestDto request
	) {
		BankQrCodeUrlResponseDto bankQrCodeUrlResponseDto = bankService.getQrCodeUrl(request);

		String url = bankQrCodeUrlResponseDto.getQrCodeUrl();

		ResponseEntity<byte[]> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				null,
				byte[].class
		);

		return ResponseEntity.ok()
				.contentType(MediaType.IMAGE_PNG)
				.body(response.getBody());
	}

}