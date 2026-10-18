package com.mezon.classmanagement.backend.common.validator;

import com.mezon.classmanagement.backend.common.constant.FileTypeUtils;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import org.springframework.web.multipart.MultipartFile;

public final class FileValidator {

	public static void validateFileType(MultipartFile multipartFile) {
		String mimeType;
		try {
			mimeType = FileUtils.detectMimeTypeFromMultipartFile(multipartFile);
		} catch (Exception e) {
			mimeType = "";
		}

		if (!FileTypeUtils.isValidExtension(FileUtils.getExtension(multipartFile))) {
			FileTypeUtils.invalidExtension();
		}
		if (!FileTypeUtils.isValidMimeType(mimeType)) {
			FileTypeUtils.invalidMimeType();
		}
	}

}