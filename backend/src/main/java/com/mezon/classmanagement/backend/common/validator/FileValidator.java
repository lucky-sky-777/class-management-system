package com.mezon.classmanagement.backend.common.validator;

import com.mezon.classmanagement.backend.common.constant.FileConstant;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import org.springframework.web.multipart.MultipartFile;

public final class FileValidator {

	public static void validateFileType(MultipartFile multipartFile) {
		boolean validExtension = FileConstant.ALLOWED_EXTENSION_SET
				.contains(
						FileUtils.getExtension(multipartFile)
				);
		boolean validMimeType = FileConstant.ALLOWED_MIME_TYPE_SET
				.contains(
						FileUtils.getMimeType(multipartFile)
				);

		if (!validExtension) {
			throw new GlobalException(
					GlobalException.Type.INVALID_REQUEST,
					"Unsupported file extension. Supported extensions: "
							+ String.join(", ", FileConstant.ALLOWED_EXTENSION_SET)
			);
		}
		if (!validMimeType) {
			throw new GlobalException(
					GlobalException.Type.INVALID_REQUEST,
					"Unsupported file mime type. Supported mime types: "
							+ String.join(", ", FileConstant.ALLOWED_MIME_TYPE_SET)
			);
		}
	}

}