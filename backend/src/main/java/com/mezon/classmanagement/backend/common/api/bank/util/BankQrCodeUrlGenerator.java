package com.mezon.classmanagement.backend.common.api.bank.util;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.util.EnumUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({WarningConstant.UNUSED})
public final class BankQrCodeUrlGenerator {

	private static final String BASE_URL = "https://img.vietqr.io/image";

	private static final String GENERATE_WITH_NO_INFO_SUFFIX = "compact";
	private static final String GENERATE_WITH_BASIC_INFO_SUFFIX = "compact2";
	private static final String GENERATE_WITH_FULL_INFO_SUFFIX = "print";
	private static final String GENERATE_WITH_QR_ONLY = "qr_only";

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@Getter
	@AllArgsConstructor
	public enum ImageType {

		FULL_INFO(GENERATE_WITH_FULL_INFO_SUFFIX),
		BASIC_INFO(GENERATE_WITH_BASIC_INFO_SUFFIX),
		NO_INFO(GENERATE_WITH_NO_INFO_SUFFIX),
		QR_ONLY(GENERATE_WITH_QR_ONLY);

		String suffix;

	}

	public static Map<String, ImageType> imageTypeMap = new HashMap<>();
	static {
		for (ImageType imageType : EnumUtils.toList(ImageType.class)) {
			imageTypeMap.put(normalizeImageTypeName(imageType.name()), imageType);
		}
	}

	private static final String IMAGE_EXTENSION = "png";

	private static final String INFO_AMOUNT = "amount";
	private static final String INFO_NOTES = "addInfo";
	private static final String INFO_ACCOUNT_NAME = "accountName";

	public static boolean isValidImageType(String imageTypeName) {
		return imageTypeMap.containsKey(normalizeImageTypeName(imageTypeName));
	}

	public static String normalizeImageTypeName(String imageTypeName) {
		return imageTypeName
				.replace("_", "-")
				.replace(" ", "")
				.toLowerCase(Locale.ROOT);
	}

	public static String generate(ImageType imageType, String bankCode, String accountNumber, String accountName, Long amount, String notes) {
//		if (bankCode == null) {
//			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Bank code cannot be null");
//		}
//		if (accountNumber == null) {
//			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Account number cannot be null");
//		}

		StringBuilder stringBuilder = new StringBuilder(BASE_URL);

		stringBuilder.append("/").append(bankCode);
		stringBuilder.append("-").append(accountNumber);
		stringBuilder.append("-").append(
				imageType.getSuffix()
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