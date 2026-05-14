package com.mezon.classmanagement.backend.common.api.bank.util;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({WarningConstant.UNUSED})
public final class BankQrCodeUrlGenerator {

	private static final String BASE_URL = "https://img.vietqr.io/image";

	private static final String GENERATE_WITHOUT_INFO_SUFFIX = "compact";
	private static final String GENERATE_WITH_BASIC_INFO_SUFFIX = "compact2";
	private static final String GENERATE_WITH_FULL_INFO_SUFFIX = "print";
	private static final String GENERATE_WITH_QR_ONLY = "qr_only";

	private static final String IMAGE_EXTENSION = "png";

	private static final String INFO_AMOUNT = "amount";
	private static final String INFO_NOTES = "addInfo";
	private static final String INFO_ACCOUNT_NAME = "accountName";

	public static String generate(String bankCode, String accountNumber, String accountName, Long amount, String notes) {
		if (bankCode == null) {
			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Bank code cannot be null");
		}
		if (accountNumber == null) {
			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Account number cannot be null");
		}

		StringBuilder stringBuilder = new StringBuilder(BASE_URL);

		stringBuilder.append("/").append(bankCode);
		stringBuilder.append("-").append(accountNumber);
		stringBuilder.append("-").append(
				GENERATE_WITH_FULL_INFO_SUFFIX
				//(accountName == null || amount == null)
				//? GENERATE_WITHOUT_INFO_SUFFIX : GENERATE_WITH_INFO_SUFFIX
		);
		stringBuilder.append(".").append(IMAGE_EXTENSION);

		List<String> queryParams = new ArrayList<>();

		if (accountName != null) {
			queryParams.add(INFO_ACCOUNT_NAME + "=" + URLEncoder.encode(accountName, StandardCharsets.UTF_8));
		}
		if (amount != null) {
			queryParams.add(INFO_AMOUNT + "=" + amount);
		}
		if (notes != null) {
			queryParams.add(INFO_NOTES + "=" + URLEncoder.encode(notes, StandardCharsets.UTF_8));
		}

		if (!queryParams.isEmpty()) {
			stringBuilder.append("?");
			stringBuilder.append(String.join("&", queryParams));
		}

		return stringBuilder.toString();
	}

}