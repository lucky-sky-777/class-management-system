package com.mezon.classmanagement.backend.common.api.bank.service;

import com.mezon.classmanagement.backend.common.api.bank.dto.request.GetBankQrCodeRequestDto;
import com.mezon.classmanagement.backend.common.api.bank.dto.response.BankQrCodeUrlResponseDto;
import com.mezon.classmanagement.backend.common.api.bank.dto.response.vietqr.VietQrBankListResponseDto;
import com.mezon.classmanagement.backend.common.api.bank.dto.response.vietqr.VietQrBankResponseDto;
import com.mezon.classmanagement.backend.common.api.bank.util.BankQrCodeUrlGenerator;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class BankService {

	WebClient vietQrWebClient;

	public List<VietQrBankResponseDto> getBankList() {
		VietQrBankListResponseDto response = vietQrWebClient
				.get()
				.uri("/v2/banks")
				.retrieve()
				.bodyToMono(VietQrBankListResponseDto.class)
				.block();

		if (response == null) {
			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, "Internal server error");
		}

		return response.getData();
	}

	public BankQrCodeUrlResponseDto getQrCodeUrl(String imageTypeName, GetBankQrCodeRequestDto request) {
		if (!BankQrCodeUrlGenerator.isValidImageType(imageTypeName)) {
			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Invalid image type");
		}
		if (request.getBankCode() == null) {
			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Bank code cannot be null");
		}
		if (request.getBankAccountNumber() == null) {
			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Account number cannot be null");
		}

		return BankQrCodeUrlResponseDto.builder()
				.bankQrCodeUrl(
						BankQrCodeUrlGenerator.generate(
								BankQrCodeUrlGenerator.imageTypeMap.get(BankQrCodeUrlGenerator.normalizeImageTypeName(imageTypeName)),
								request.getBankCode(),
								request.getBankAccountNumber(),
								request.getBankAccountName(),
								request.getTransferAmount(),
								request.getTransferNote()
						)
				)
				.build();
	}

}